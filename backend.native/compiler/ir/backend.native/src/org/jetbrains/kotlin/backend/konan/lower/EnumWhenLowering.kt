package org.jetbrains.kotlin.backend.konan.lower

import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.konan.Context
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.descriptors.IrTemporaryVariableDescriptorImpl
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.util.getArguments
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.isNullable

// Look for when-constructs where subject is enum entry.
// Replace branches that are comparisons with compile-time known enum entries
// with comparisons of ordinals.
internal class EnumWhenLowering(private val context: Context) : IrElementTransformerVoid(), FileLoweringPass {

    private val areEqualByValue = context.ir.symbols.areEqualByValue.first {
        it.owner.valueParameters[0].type == context.builtIns.intType
    }

    override fun lower(irFile: IrFile) {
        visitFile(irFile)
    }

    // Checks that irBlock satisfies all constrains of this lowering.
    // 1. Block's origin is WHEN
    // 2. Subject of `when` is variable of enum type
    // NB: See BranchingExpressionGenerator in Kotlin sources to get insight about
    // `when` block translation to IR.
    private fun shouldLower(irBlock: IrBlock): Boolean {
        if (irBlock.origin != IrStatementOrigin.WHEN) {
            return false
        }
        // when-block with subject should have two children: temporary variable and when itself.
        if (irBlock.statements.size != 2) {
            return false
        }
        val subject = irBlock.statements[0] as IrVariable
        // Subject should not be nullable because we will access the `ordinal` property.
        if (subject.type.isNullable()) {
            return false
        }
        // Check that subject is enum entry.
        val enumClass = subject.type.constructor.declarationDescriptor as? ClassDescriptor
                ?: return false
        return enumClass.kind == ClassKind.ENUM_CLASS
    }

    override fun visitBlock(expression: IrBlock): IrExpression {
        if (!shouldLower(expression)) {
            return super.visitBlock(expression)
        }
        // Will be initialized only when we found a branch that compares
        // subject with compile-time known enum entry.
        val ordinalVariable: IrVariable by lazy {
            val variable = createEnumOrdinalVariable(expression)
            expression.statements.add(1, variable)
            variable
        }
        val whenExpr = expression.statements[1] as IrWhen
        // ordinalVariable should be passed lazily because it should be created only once.
        lowerWhen(whenExpr) { ordinalVariable }
        // Process comma-separated cases.
        whenExpr.branches.filter { isCommaSeparatedCondition(it.condition) }
                .map { it.condition as IrWhen }
                .forEach { lowerWhen(it) { ordinalVariable } }
        // Process nested when constructs.
        expression.transformChildrenVoid(this)
        return expression
    }

    private fun isCommaSeparatedCondition(condition: IrExpression): Boolean =
            condition is IrWhen && condition.origin == IrStatementOrigin.WHEN_COMMA

    private fun lowerWhen(whenExpr: IrWhen, ordinalVariableProvider: () -> IrVariable) {

        fun lowerExpressionIfNeeded(expression: IrExpression) = if (isComparisonWithEnumEntry(expression)) {
            createComparisonOfOrdinals(expression, ordinalVariableProvider)
        } else {
            expression
        }

        whenExpr.branches.forEach {
            it.condition = lowerExpressionIfNeeded(it.condition)
        }
        // If we're processing when comma-separated condition then body of the last branch should be comparison.
        if (whenExpr.branches.isNotEmpty() && whenExpr.branches.last() is IrConst<*>) {
            val lastBranch  = whenExpr.branches.last()
            lastBranch.result = lowerExpressionIfNeeded(lastBranch.result)
        }
    }

    private fun createComparisonOfOrdinals(expression: IrExpression, ordinalVariableProvider: () -> IrVariable): IrCall {
        // Checked before in isComparisonWithEnumEntry
        val eqEqCall = expression as IrMemberAccessExpression
        val entry = eqEqCall.getArguments()[1].second as IrGetEnumValue
        val entryOrdinal = context.specialDeclarationsFactory.getEnumEntryOrdinal(entry.descriptor)
        // replace condition with trivial comparison of ordinals
        val ordinalVariable = ordinalVariableProvider()
        return IrCallImpl(eqEqCall.startOffset, eqEqCall.endOffset, areEqualByValue).apply {
            putValueArgument(0, IrConstImpl.int(entry.startOffset, entry.endOffset, context.builtIns.intType, entryOrdinal))
            putValueArgument(1, IrGetValueImpl(ordinalVariable.startOffset, ordinalVariable.endOffset, ordinalVariable.symbol))
        }
    }

    // We are looking for branch that is a comparison of the subject and another enum entry.
    // Both entries should belong to the same class.
    private fun isComparisonWithEnumEntry(expression: IrExpression): Boolean {
        val call = expression as? IrMemberAccessExpression
                ?: return false
        if (call.origin != IrStatementOrigin.EQEQ) {
            return false
        }
        // Types should be the same.
        val callArgs = call.getArguments()
        if (callArgs.size == 2 && callArgs[1].second.type == callArgs[0].second.type) {
            // Check that we're comparing with enum entry
            return callArgs[1].second is IrGetEnumValue
        }
        return false
    }

    private fun createEnumOrdinalVariable(irBlock: IrBlock): IrVariable {
        val tempVariable = irBlock.statements[0] as IrVariable
        val ordinalPropertyGetter = context.ir.symbols.enum.getPropertyGetter("ordinal")!!
        val getOrdinal = IrCallImpl(tempVariable.startOffset, tempVariable.endOffset, ordinalPropertyGetter).apply {
            dispatchReceiver = IrGetValueImpl(tempVariable.startOffset, tempVariable.endOffset, tempVariable.symbol)
        }
        // Create temporary variable for subject's ordinal.
        val ordinalDescriptor = IrTemporaryVariableDescriptorImpl(tempVariable.descriptor.containingDeclaration,
                Name.identifier(tempVariable.name.asString() + "_ordinal"), context.builtIns.intType)
        return IrVariableImpl(tempVariable.startOffset, tempVariable.endOffset,
                IrDeclarationOrigin.IR_TEMPORARY_VARIABLE, ordinalDescriptor, getOrdinal)
    }
}

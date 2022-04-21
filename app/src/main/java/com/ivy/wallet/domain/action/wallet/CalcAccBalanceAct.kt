package com.ivy.wallet.domain.action.wallet

import arrow.core.nonEmptyListOf
import com.ivy.wallet.domain.action.FPAction
import com.ivy.wallet.domain.action.account.AccTrnsAct
import com.ivy.wallet.domain.action.then
import com.ivy.wallet.domain.data.entity.Account
import com.ivy.wallet.domain.fp.account.AccountValueFunctions
import com.ivy.wallet.domain.fp.account.calcAccValues
import com.ivy.wallet.domain.fp.data.ClosedTimeRange
import java.math.BigDecimal
import javax.inject.Inject

class CalcAccBalanceAct @Inject constructor(
    private val accTrnsAct: AccTrnsAct
) : FPAction<CalcAccBalanceAct.Input, CalcAccBalanceAct.Output>() {

    override suspend fun Input.recipe(): suspend () -> Output = suspend {
        AccTrnsAct.Input(
            accountId = account.id,
            range = ClosedTimeRange.allTimeIvy()
        )
    } then accTrnsAct then { accTrns ->
        calcAccValues(
            accountId = account.id,
            accountsTrns = accTrns,
            valueFunctions = nonEmptyListOf(AccountValueFunctions::balance)
        ).head
    } then { balance ->
        Output(
            account = account,
            balance = balance
        )
    }

    data class Input(
        val account: Account
    )

    data class Output(
        val account: Account,
        val balance: BigDecimal
    )
}
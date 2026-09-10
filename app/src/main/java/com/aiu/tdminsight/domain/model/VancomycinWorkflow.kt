package com.aiu.tdminsight.domain.model

/**
 * The three required Vancomycin TDM workflows (Case Study §3). The
 * selected value is what should drive which input fields the UI shows
 * — never display every field from every workflow at once.
 */
enum class VancomycinWorkflow {
    PRE,
    POST,
    PRE_POST
}

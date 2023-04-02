package com.elixsr.portforwarder.validators

import android.util.Log
import com.elixsr.portforwarder.exceptions.RuleValidationException
import com.elixsr.portforwarder.models.RuleModel
import com.elixsr.portforwarder.util.IpAddressValidator
import com.elixsr.portforwarder.util.RuleHelper

/**
 * Created by Cathan on 25/07/2017.
 */
class RuleModelValidator {
    @Throws(RuleValidationException::class)
    fun validate(ruleModel: RuleModel): Boolean {
        return validateRule(ruleModel)
    }

    companion object {
        private const val TAG = "RuleModelValidator"

        @Throws(RuleValidationException::class)
        fun validateRule(ruleModel: RuleModel): Boolean {
            return validateRuleName(ruleModel.name) &&
                    validateRuleFromPort(ruleModel.fromPort) &&
                    validateRuleTargetPort(ruleModel.targetPort) &&
                    validateRuleTargetIpAddress(ruleModel.targetIpAddress) &&
                    validateRuleTargetIpAddressSyntax(ruleModel.targetIpAddress)
        }

        @JvmStatic
        @Throws(RuleValidationException::class)
        fun validateRuleName(ruleName: String?): Boolean {
            if (ruleName == null || ruleName.length == 0) {
                throw RuleValidationException("You must enter a name")
            }
            return true
        }

        @Throws(RuleValidationException::class)
        fun validateRuleFromPort(ruleFromPort: Int): Boolean {
            if (ruleFromPort < RuleHelper.MIN_PORT_VALUE || ruleFromPort > RuleHelper.MAX_PORT_VALUE) {
                throw RuleValidationException(String.format("From port must be a value greater than or equal to %s and less than or equal to %s ", RuleHelper.MIN_PORT_VALUE, RuleHelper.MAX_PORT_VALUE))
            }
            return true
        }

        @JvmStatic
        @Throws(RuleValidationException::class)
        fun validateRuleFromPort(ruleFromPort: String?): Boolean {
            if (ruleFromPort != null && ruleFromPort.length > 0) {
                return validateRuleFromPort(ruleFromPort.toInt())
            }
            throw RuleValidationException(String.format("From port must be a value greater than or equal to %s and less than or equal to %s ", RuleHelper.MIN_PORT_VALUE, RuleHelper.MAX_PORT_VALUE))
        }

        @Throws(RuleValidationException::class)
        fun validateRuleTargetPort(ruleTargetPort: Int): Boolean {
            if (ruleTargetPort < RuleHelper.TARGET_MIN_PORT || ruleTargetPort > RuleHelper.MAX_PORT_VALUE) {
                throw RuleValidationException(String.format("Please enter a value greater than or equal to %s and less than or equal to %s ", RuleHelper.TARGET_MIN_PORT, RuleHelper.MAX_PORT_VALUE))
            }
            return true
        }

        @JvmStatic
        @Throws(RuleValidationException::class)
        fun validateRuleTargetPort(ruleTargetPort: String?): Boolean {
            if (ruleTargetPort != null && ruleTargetPort.length > 0) {
                return validateRuleTargetPort(ruleTargetPort.toInt())
            }
            Log.e(TAG, "No target port was included")
            throw RuleValidationException(String.format("Please enter a value greater than or equal to %s and less than or equal to %s ", RuleHelper.TARGET_MIN_PORT, RuleHelper.MAX_PORT_VALUE))
        }

        @JvmStatic
        @Throws(RuleValidationException::class)
        fun validateRuleTargetIpAddress(ruleTargetIpAddress: String?): Boolean {
            if (ruleTargetIpAddress != null && ruleTargetIpAddress.length > 0) {
                return validateRuleTargetIpAddressSyntax(ruleTargetIpAddress)
            }
            throw RuleValidationException("You must enter a target address")
        }

        @Throws(RuleValidationException::class)
        fun validateRuleTargetIpAddressSyntax(ruleTargetIpAddress: String?): Boolean {
            if (!IpAddressValidator().validate(ruleTargetIpAddress)) {
                throw RuleValidationException("Target IP address was not valid")
            }
            Log.i(TAG, "validateRuleTargetIpAddressSyntax: TARGET IP VALID")
            return true
        }
    }
}
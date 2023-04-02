package com.elixsr.portforwarder.validators;

import android.util.Log;

import com.elixsr.portforwarder.exceptions.RuleValidationException;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.util.IpAddressValidator;
import com.elixsr.portforwarder.util.RuleHelper;

/**
 * Created by Cathan on 25/07/2017.
 */

public class RuleModelValidator {

    private static final String TAG = "RuleModelValidator";


    public boolean validate(RuleModel ruleModel) throws RuleValidationException {
        return validateRule(ruleModel);
    }

    public static boolean validateRule(RuleModel ruleModel) throws RuleValidationException {
        return validateRuleName(ruleModel.getName()) &&
                validateRuleFromPort(ruleModel.getFromPort()) &&
                validateRuleTargetPort(ruleModel.getTargetPort()) &&
                validateRuleTargetIpAddress(ruleModel.getTargetIpAddress()) &&
                validateRuleTargetIpAddressSyntax(ruleModel.getTargetIpAddress());
    }

    public static boolean validateRuleName(String ruleName) throws RuleValidationException {
        if (ruleName == null || ruleName.length() == 0) {
            throw new RuleValidationException("You must enter a name");
        }

        return true;
    }

    public static boolean validateRuleFromPort(int ruleFromPort) throws RuleValidationException {
        if (ruleFromPort < RuleHelper.MIN_PORT_VALUE || ruleFromPort > RuleHelper.MAX_PORT_VALUE) {
            throw new RuleValidationException(String.format("From port must be a value greater than or equal to %s and less than or equal to %s ", RuleHelper.MIN_PORT_VALUE, RuleHelper.MAX_PORT_VALUE));
        }

        return true;
    }

    public static boolean validateRuleFromPort(String ruleFromPort) throws RuleValidationException {
        if (ruleFromPort != null && ruleFromPort.length() > 0) {
            return validateRuleFromPort(Integer.parseInt(ruleFromPort));
        }

        throw new RuleValidationException(String.format("From port must be a value greater than or equal to %s and less than or equal to %s ", RuleHelper.MIN_PORT_VALUE, RuleHelper.MAX_PORT_VALUE));
    }

    public static boolean validateRuleTargetPort(int ruleTargetPort) throws RuleValidationException {
        if (ruleTargetPort < RuleHelper.TARGET_MIN_PORT || ruleTargetPort > RuleHelper.MAX_PORT_VALUE) {
            throw new RuleValidationException(String.format("Please enter a value greater than or equal to %s and less than or equal to %s ", RuleHelper.TARGET_MIN_PORT, RuleHelper.MAX_PORT_VALUE));
        }

        return true;
    }

    public static boolean validateRuleTargetPort(String ruleTargetPort) throws RuleValidationException {
        if (ruleTargetPort != null && ruleTargetPort.length() > 0) {
            return validateRuleTargetPort(Integer.parseInt(ruleTargetPort));
        }

        Log.e(TAG, "No target port was included");
        throw new RuleValidationException(String.format("Please enter a value greater than or equal to %s and less than or equal to %s ", RuleHelper.TARGET_MIN_PORT, RuleHelper.MAX_PORT_VALUE));
    }

    public static boolean validateRuleTargetIpAddress(String ruleTargetIpAddress) throws RuleValidationException {
        if (ruleTargetIpAddress != null && ruleTargetIpAddress.length() > 0) {
            return validateRuleTargetIpAddressSyntax(ruleTargetIpAddress);
        }

        throw new RuleValidationException("You must enter a target address");
    }

    public static boolean validateRuleTargetIpAddressSyntax(String ruleTargetIpAddress) throws RuleValidationException {
        if (!new IpAddressValidator().validate(ruleTargetIpAddress)) {
            throw new RuleValidationException("Target IP address was not valid");
        }

        Log.i(TAG, "validateRuleTargetIpAddressSyntax: TARGET IP VALID");
        return true;
    }

}

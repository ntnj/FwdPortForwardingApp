package com.elixsr.portforwarder.validators;

import android.util.Log;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.exceptions.RuleValidationException;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.util.RuleHelper;

/**
 * Created by Cathan on 25/07/2017.
 */

public class RuleModelValidator implements Validator<RuleModel> {

    private static final String INVALID_PORT_ERROR_MESSAGE = "Please enter a value greater than or equal to %s and less than or equal to %s";


    @Override
    public boolean validate(RuleModel ruleModel) throws RuleValidationException {

        return true;
    }

    public boolean validateRuleName(String ruleName) {
        if(ruleName == null || ruleName.length() <= 0){
            return false;
        }

        return true;
    }

    public boolean validateRuleFromPort(int ruleFromPort) {
        if (ruleFromPort <= 0 || ruleFromPort < RuleHelper.MIN_PORT_VALUE || ruleFromPort > RuleHelper.MAX_PORT_VALUE) {
            return false;
        }

        return true;
    }

    public boolean validateRuleTargetPort(int ruleTargetPort) {
        return true;
    }

    public boolean validateRuleTargetIpAddress(String ruleTargetIpAddress) {
        return true;
    }
}

package com.elixsr.portforwarder.ui.preferences;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.elixsr.portforwarder.R;
import com.elixsr.portforwarder.adapters.RuleListJsonValidator;
import com.elixsr.portforwarder.adapters.RuleListTargetJsonSerializer;
import com.elixsr.portforwarder.dao.RuleDao;
import com.elixsr.portforwarder.db.RuleDbHelper;
import com.elixsr.portforwarder.exceptions.RuleValidationException;
import com.elixsr.portforwarder.models.RuleModel;
import com.elixsr.portforwarder.ui.BaseActivity;
import com.elixsr.portforwarder.ui.MainActivity;
import com.elixsr.portforwarder.util.InterfaceHelper;
import com.elixsr.portforwarder.validators.RuleModelValidator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ImportRulesActivity extends BaseActivity {

    public static final String IMPORTED_RULE_DATA = "imported_rule_data";
    private final String TAG = "ImportRulesActivity";
    protected Spinner fromInterfaceSpinner;
    protected ArrayAdapter<String> fromSpinnerAdapter;
    private RuleDao ruleDao;
    private Gson gson;
    private List<RuleModel> ruleModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_import_rules);

        Toolbar toolbar = getActionBarToolbar();
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_close_24dp);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        TextView importRulesText = findViewById(R.id.import_rules_count_text);
        Button importRulesButton = findViewById(R.id.import_rules_button);
        ImageView helpButton = findViewById(R.id.help_button);

        ruleModels = new LinkedList<>();

        constructDetailUi();

        ruleDao = new RuleDao(new RuleDbHelper(this));
        Bundle extras = getIntent().getExtras();
        Uri data;
        gson = new GsonBuilder()
                .registerTypeAdapter(InetSocketAddress.class, new RuleListTargetJsonSerializer())
                .registerTypeAdapter(RuleModel.class, new RuleListJsonValidator())
                .create();
        if(extras != null) {
            data = Uri.parse(extras.getString(IMPORTED_RULE_DATA));
            parseRules(data);
        }

        // We shouldn't continue if we don't have any rules.
        if(ruleModels.size() == 0) {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainActivityIntent);
            finish();
            return;
        }

        // TODO: Expose as localised strings
        String importText = "You are about to import <b>" + ruleModels.size() + "</b> rule, configure the interface and target address below";
        if(ruleModels.size() > 1 ) {
            importText = "You are about to import <b>" + ruleModels.size() + "</b> rules, configure the interface and target address below";
        }

        importRulesText.setText(Html.fromHtml(importText));
        importRulesButton.setText("IMPORT " + ruleModels.size() + " RULES");

        importRulesButton.setOnClickListener(v -> importRules());
        helpButton.setOnClickListener(v -> {
            Intent mainActivityIntent = new Intent(v.getContext(), SupportSiteActivity.class);
            startActivity(mainActivityIntent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void parseRules(Uri data) {
        boolean ruleFailedValidation = false;
        int successfulRuleAdditions = 0;
        JsonReader reader;
        Type collectionType = new TypeToken<Collection<RuleModel>>() {}.getType();

        try {
            InputStream fileContentStream = getContentResolver().openInputStream(data);
            reader = new JsonReader(new InputStreamReader(fileContentStream));
            List<RuleModel> allRuleModels = gson.fromJson(reader, collectionType);
            for (RuleModel ruleModel : allRuleModels) {
                try {
                    if (RuleModelValidator.validateRule(ruleModel)) {
                        successfulRuleAdditions++;
                        ruleModels.add(ruleModel);
                    }
                } catch (RuleValidationException e) {
                    ruleFailedValidation = true;
                }
            }

            if (ruleFailedValidation) {
                Toast.makeText(getApplicationContext(), "Some rules failed validation. Imported " + successfulRuleAdditions + " rules.", Toast.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Error importing rules - No valid file found.", Toast.LENGTH_LONG).show();
        } catch (JsonSyntaxException e) {
            Toast.makeText(getApplicationContext(), "Error importing rules - JSON file is malformed.", Toast.LENGTH_LONG).show();
        } catch (JsonParseException e) {
            Toast.makeText(getApplicationContext(), "Error importing rules - Rule list is invalid.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importRules() {

        boolean validationError = false;

        String targetIpAddress = null;

        TextInputEditText targetIpAddressText = findViewById(R.id.new_rule_target_ip_address);

        // Validate the input, and show error message if wrong
        try {
            if (RuleModelValidator.validateRuleTargetIpAddress(targetIpAddressText.getText().toString())) {
                targetIpAddress = targetIpAddressText.getText().toString();
            }
        } catch (RuleValidationException e) {
            targetIpAddressText.setError(e.getMessage());
            validationError = true;
        }

        if(validationError) {
            return;
        }

        for (RuleModel ruleModel : ruleModels) {

            // Create an InetSocketAddress object using data
            InetSocketAddress target = new InetSocketAddress(targetIpAddress, ruleModel.getTargetPort());
            ruleModel.setTarget(target);

            Spinner fromInterfaceSpinner = findViewById(R.id.from_interface_spinner);
            String selectedFromInterface = fromInterfaceSpinner.getSelectedItem().toString();
            ruleModel.setFromInterfaceName(selectedFromInterface);

            ruleDao.insertRule(ruleModel);
        }

        Toast.makeText(getApplicationContext(), "Imported " + ruleModels.size() + " rules.", Toast.LENGTH_LONG).show();

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();


    }

    protected void constructDetailUi() {

        // Generate interfaces
        List<String> interfaces;
        try {
            interfaces = InterfaceHelper.generateInterfaceNamesList();


        } catch (SocketException e) {
            Log.i(TAG, "Error generating Interface list", e);

            // Show toast and move to main screen
            Toast.makeText(this, "Problem locating network interfaces. Please refer to 'help' to " +
                            "assist with troubleshooting.",
                    Toast.LENGTH_LONG).show();
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
            return;
        }

        // Check to ensure we have some interface to show!
        if (interfaces.isEmpty()) {
            Toast.makeText(this, "Could not locate any network interfaces. Please refer to 'help'" +
                            " to assist with troubleshooting.",
                    Toast.LENGTH_LONG).show();
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
            return;
        }


        // Set up protocol spinner/dropdown
        fromInterfaceSpinner = findViewById(R.id.from_interface_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        fromSpinnerAdapter = new ArrayAdapter<>(this, R.layout.my_spinner, interfaces);

        // Specify the layout to use when the list of choices appears
        fromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the protocolAdapter to the spinner
        fromInterfaceSpinner.setAdapter(fromSpinnerAdapter);

    }
}
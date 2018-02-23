package com.tobipristupin.simplerun.ui.settings;

import android.content.DialogInterface;

import com.tobipristupin.simplerun.data.interfaces.PreferencesRepository;
import com.tobipristupin.simplerun.data.model.Distance;
import com.tobipristupin.simplerun.ui.settings.dialogs.DistanceUnitDialog;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Presenter for SettingsView implementation
 */

public class SettingsPresenter {

    private SettingsView view;
    private PreferencesRepository preferencesRepository;

    public SettingsPresenter(SettingsView view, PreferencesRepository repository) {
        this.view = view;
        this.preferencesRepository = repository;
    }

    public void onCreateView() {
        view.setDistanceUnitText(getDistanceUnit());
    }

    public void onDistanceUnitClick() {
        DistanceUnitDialog.OnClickListener listener = new DistanceUnitDialog.OnClickListener() {
            @Override
            public void onOptionSelected(Distance.Unit unit) {
                view.setDistanceUnitText(getDistanceUnit());
                preferencesRepository.setDistanceUnit(unit);
            }
        };

        view.showDistanceUnitDialog(listener);
    }

    public void onSignOutClick() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                view.loadLogIn();
                dialog.dismiss();
            }
        };

        view.showSignOutDialog(listener);
    }

    public void onHelpAndFeedbackClick() {
        view.sendEmailIntent("justrunapp@gmail.com");
    }

    public void onAboutClick() {
        view.showAboutDialog();
    }

    public void onActivityNotFoundError() {
        view.showNoEmailAppError();
    }

    public void onLibrariesClick() {
        view.sendLibrariesViewIntent();
    }

    private Distance.Unit getDistanceUnit() {
        return preferencesRepository.getDistanceUnit();
    }
}

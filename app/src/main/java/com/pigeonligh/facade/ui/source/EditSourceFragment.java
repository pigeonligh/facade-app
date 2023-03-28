package com.pigeonligh.facade.ui.source;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pigeonligh.facade.R;
import com.pigeonligh.facade.data.types.DataSourceItem;

public class EditSourceFragment extends DialogFragment {
    private final Answerer answerer;
    private final boolean isCreateMode;
    private final DataSourceItem initItem;

    public EditSourceFragment(Answerer answerer) {
        this.answerer = answerer;
        this.isCreateMode = true;
        this.initItem = null;
    }


    public EditSourceFragment(Answerer answerer, DataSourceItem item) {
        this.answerer = answerer;
        this.isCreateMode = false;
        this.initItem = item;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        if (isCreateMode) {
            builder.setTitle("Add Source");
            builder.setView(inflater.inflate(R.layout.fragment_edit_source, null));
            builder.setPositiveButton("Add", null);
            builder.setNegativeButton("Cancel", null);
        } else {
            builder.setTitle("Edit Source");
            builder.setView(inflater.inflate(R.layout.fragment_edit_source, null));
            builder.setPositiveButton("Edit", null);
            builder.setNegativeButton("Cancel", null);
            builder.setNeutralButton("Delete", null);
        }

        return builder.create();
    }

    @Override
    public void onResume() {
        AlertDialog dialog = (AlertDialog) getDialog();

        EditText editName = dialog.findViewById(R.id.editTextName);
        EditText editURL = dialog.findViewById(R.id.editTextURL);

        if (initItem != null) {
            editName.setText(initItem.getName());
            editURL.setText(initItem.getUrl());
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerer.toConfirm(new DataSourceItem(editName.getText().toString(), editURL.getText().toString()))) {
                    dialog.dismiss();
                }
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerer.toCancel()) {
                    dialog.dismiss();
                }
            }
        });
        if (!isCreateMode) {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerer.toDelete()) {
                        dialog.dismiss();
                    }
                }
            });
        }
        super.onResume();
    }

    public interface Answerer {
        boolean toConfirm(DataSourceItem item);

        boolean toDelete();

        boolean toCancel();
    }
}
package com.priorityrss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by nbp184 on 2016/04/29.
 */
public class MyOptionPane {

    public static final String ARG_TITLE = "title";
    public static final String ARG_MESSAGE = "message";
    private static final String ARG_OPTION_TYPE = "option type";

    public static final int OK_CANCEL_OPTION = 0;
    public static final int YES_NO_OPTION = 1;
    public static final int YES_NO_CANCEL_OPTION = 2;

    public static final int CANCEL_OPTION = 0;
    public static final int OK_OPTION = 1;
    public static final int YES_OPTION = 2;
    public static final int NO_OPTION = 3;

    public static void showMessageDialog(FragmentManager manager, String tag, String message) {
        MessageDialog frag = new MessageDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        frag.setArguments(args);
        frag.show(manager, tag);
    }

    public static void showMessageDialog(FragmentManager manager, String tag, String message, String title) {
        MessageDialog frag = new MessageDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        frag.setArguments(args);
        frag.show(manager, tag);
    }

    public static void showQuestionDialog(FragmentManager manager, String tag, String message) {
        QuestionDialog frag = new QuestionDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putInt(ARG_OPTION_TYPE, OK_CANCEL_OPTION);
        frag.setArguments(args);
        frag.show(manager, tag);
    }

    public static void showQuestionDialog(FragmentManager manager, String tag, String message, String title) {
        QuestionDialog frag = new QuestionDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_OPTION_TYPE, OK_CANCEL_OPTION);
        frag.setArguments(args);
        frag.show(manager, tag);
    }

    public static void showQuestionDialog(FragmentManager manager, String tag, String message, String title, int optionType) {
        QuestionDialog frag = new QuestionDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_OPTION_TYPE, optionType);
        frag.setArguments(args);
        frag.show(manager, tag);
    }

    public interface OnClickListener {
        void onOptionPaneClick(String tag, int returnValue);
    }


    private static class QuestionDialog extends DialogFragment {

        private OnClickListener listener = null;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if(activity instanceof OnClickListener) {
                listener = (OnClickListener)activity;
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getArguments().getString(ARG_TITLE, "Confirm"))
                    .setMessage(getArguments().getString(ARG_MESSAGE));
            switch(getArguments().getInt(ARG_OPTION_TYPE)) {
                case OK_CANCEL_OPTION:
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            returnValue(OK_OPTION);
                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    returnValue(CANCEL_OPTION);
                                }
                            });
                    break;
                case YES_NO_OPTION:
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            returnValue(YES_OPTION);
                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    returnValue(NO_OPTION);
                                }
                            });
                    break;
                case YES_NO_CANCEL_OPTION:
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            returnValue(YES_OPTION);
                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    returnValue(NO_OPTION);
                                }
                            })
                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    returnValue(CANCEL_OPTION);
                                }
                            });
                    break;
            }
            return builder.create();
        }

        private void returnValue(int option) {
            if(listener != null) {
                listener.onOptionPaneClick(getTag(), option);
            }
        }

    }

    private static class MessageDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getArguments().getString(ARG_TITLE, "Message"))
                    .setMessage(getArguments().getString(ARG_MESSAGE));
            return builder.create();
        }
    }

}

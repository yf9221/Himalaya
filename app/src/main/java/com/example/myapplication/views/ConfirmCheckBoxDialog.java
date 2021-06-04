package com.example.myapplication.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class ConfirmCheckBoxDialog extends Dialog {

    private View mconfirm;
    private View mCancel;
    private OnDialogAlbumClickLisenter mDialogAlbumClickListener=null ;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        this(context,true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        initView();
        initLisenter();
    }

    private void initLisenter() {
        mconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogAlbumClickListener != null) {
                    boolean isChecked = mCheckBox.isChecked();
                    mDialogAlbumClickListener.onConFirm(isChecked);
                    dismiss();
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogAlbumClickListener.onCancel();
                dismiss();

            }
        });
    }

    private void initView() {
        mCancel = this.findViewById(R.id.dialog_check_box_cancel);
        mconfirm = this.findViewById(R.id.dialog_check_box_confirm);
        mCheckBox = this.findViewById(R.id.dialog_check_box);

    }

    public void setOnDialogAlbumClickLisenter(OnDialogAlbumClickLisenter lisenter){
        this.mDialogAlbumClickListener=lisenter;

    }
    public interface OnDialogAlbumClickLisenter{
        void onConFirm(boolean isChecked);
        void onCancel();
    }
}

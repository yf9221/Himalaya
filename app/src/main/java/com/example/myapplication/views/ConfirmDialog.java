package com.example.myapplication.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class ConfirmDialog extends Dialog {

    private View mCancelSub;
    private View mGiveUp;
    private OnDialogAlbumClickLisenter mDialogAlbumClickListener=null ;

    public ConfirmDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context,true,null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initLisenter();
    }

    private void initLisenter() {
        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogAlbumClickListener != null) {
                    mDialogAlbumClickListener.onDialogCancel();
                    dismiss();
                    }
            }
        });
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogAlbumClickListener.onDialogSub();
                dismiss();

            }
        });
    }

    private void initView() {
        mCancelSub = this.findViewById(R.id.dialog_cancel_sub);
        mGiveUp = this.findViewById(R.id.dialog_give_up_tv);


    }

    public void setOnDialogAlbumClickLisenter(OnDialogAlbumClickLisenter lisenter){
        this.mDialogAlbumClickListener=lisenter;

    }
    public interface OnDialogAlbumClickLisenter{
        void onDialogCancel();
        void onDialogSub();
    }
}

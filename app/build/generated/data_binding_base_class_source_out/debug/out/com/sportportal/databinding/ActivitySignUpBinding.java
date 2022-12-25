// Generated by view binder compiler. Do not edit!
package com.sportportal.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputEditText;
import com.sportportal.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivitySignUpBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final AppCompatButton btnSignUp;

  @NonNull
  public final TextInputEditText edtEmail;

  @NonNull
  public final TextInputEditText edtName;

  @NonNull
  public final TextInputEditText edtPassword;

  @NonNull
  public final LinearLayout signUpForAccount;

  @NonNull
  public final TextView tvSignIn;

  private ActivitySignUpBinding(@NonNull RelativeLayout rootView,
      @NonNull AppCompatButton btnSignUp, @NonNull TextInputEditText edtEmail,
      @NonNull TextInputEditText edtName, @NonNull TextInputEditText edtPassword,
      @NonNull LinearLayout signUpForAccount, @NonNull TextView tvSignIn) {
    this.rootView = rootView;
    this.btnSignUp = btnSignUp;
    this.edtEmail = edtEmail;
    this.edtName = edtName;
    this.edtPassword = edtPassword;
    this.signUpForAccount = signUpForAccount;
    this.tvSignIn = tvSignIn;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySignUpBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySignUpBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_sign_up, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySignUpBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_sign_up;
      AppCompatButton btnSignUp = ViewBindings.findChildViewById(rootView, id);
      if (btnSignUp == null) {
        break missingId;
      }

      id = R.id.edt_email;
      TextInputEditText edtEmail = ViewBindings.findChildViewById(rootView, id);
      if (edtEmail == null) {
        break missingId;
      }

      id = R.id.edt_name;
      TextInputEditText edtName = ViewBindings.findChildViewById(rootView, id);
      if (edtName == null) {
        break missingId;
      }

      id = R.id.edt_password;
      TextInputEditText edtPassword = ViewBindings.findChildViewById(rootView, id);
      if (edtPassword == null) {
        break missingId;
      }

      id = R.id.sign_up_for_account;
      LinearLayout signUpForAccount = ViewBindings.findChildViewById(rootView, id);
      if (signUpForAccount == null) {
        break missingId;
      }

      id = R.id.tv_sign_in;
      TextView tvSignIn = ViewBindings.findChildViewById(rootView, id);
      if (tvSignIn == null) {
        break missingId;
      }

      return new ActivitySignUpBinding((RelativeLayout) rootView, btnSignUp, edtEmail, edtName,
          edtPassword, signUpForAccount, tvSignIn);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
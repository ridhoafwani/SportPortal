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

public final class ActivityPasswordBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final AppCompatButton btnReset;

  @NonNull
  public final TextInputEditText edtEmail;

  @NonNull
  public final LinearLayout signUpForAccount;

  @NonNull
  public final TextView tvLogin;

  private ActivityPasswordBinding(@NonNull RelativeLayout rootView,
      @NonNull AppCompatButton btnReset, @NonNull TextInputEditText edtEmail,
      @NonNull LinearLayout signUpForAccount, @NonNull TextView tvLogin) {
    this.rootView = rootView;
    this.btnReset = btnReset;
    this.edtEmail = edtEmail;
    this.signUpForAccount = signUpForAccount;
    this.tvLogin = tvLogin;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityPasswordBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityPasswordBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_password, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityPasswordBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_reset;
      AppCompatButton btnReset = ViewBindings.findChildViewById(rootView, id);
      if (btnReset == null) {
        break missingId;
      }

      id = R.id.edt_email;
      TextInputEditText edtEmail = ViewBindings.findChildViewById(rootView, id);
      if (edtEmail == null) {
        break missingId;
      }

      id = R.id.sign_up_for_account;
      LinearLayout signUpForAccount = ViewBindings.findChildViewById(rootView, id);
      if (signUpForAccount == null) {
        break missingId;
      }

      id = R.id.tv_login;
      TextView tvLogin = ViewBindings.findChildViewById(rootView, id);
      if (tvLogin == null) {
        break missingId;
      }

      return new ActivityPasswordBinding((RelativeLayout) rootView, btnReset, edtEmail,
          signUpForAccount, tvLogin);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
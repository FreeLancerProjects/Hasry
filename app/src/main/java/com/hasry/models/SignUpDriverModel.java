package com.hasry.models;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.hasry.BR;
import com.hasry.R;


public class SignUpDriverModel extends BaseObservable {
    private String name;
    private String phone_code;
    private String phone;
    private String email;
    private String city;
    private String neigborhood;

    private boolean isTermsAccepted;
    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();


    public boolean isDataValid(Context context)
    {
        if (!name.trim().isEmpty()&&
                !email.trim().isEmpty()&&
                !city.trim().isEmpty()&&
                !neigborhood.trim().isEmpty()&&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()&&
                isTermsAccepted
        ){
            error_name.set(null);
            error_email.set(null);

            return true;
        }else
            {
                if (name.trim().isEmpty())
                {
                    error_name.set(context.getString(R.string.field_required));

                }else
                    {
                        error_name.set(null);

                    }

                if (email.trim().isEmpty())
                {
                    error_email.set(context.getString(R.string.field_required));

                }else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
                {
                    error_email.set(context.getString(R.string.inv_email));

                }else {
                    error_email.set(null);

                }

                if (city.trim().isEmpty())
                {
                    Toast.makeText(context, R.string.ch_city, Toast.LENGTH_SHORT).show();

                }
                if (neigborhood.trim().isEmpty())
                {
                    Toast.makeText(context, R.string.ch_neigborhood, Toast.LENGTH_SHORT).show();

                }

                if (!isTermsAccepted)
                {
                    Toast.makeText(context, R.string.cannot_signup, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
    }
    public SignUpDriverModel() {
        setName("");
        setEmail("");
        setCity("");
setNeigborhood("");
        isTermsAccepted = false;
    }



    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);

    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }


    public String getNeigborhood() {
        return neigborhood;
    }

    public void setNeigborhood(String neigborhood) {
        this.neigborhood = neigborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isTermsAccepted() {
        return isTermsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        isTermsAccepted = termsAccepted;
    }
}

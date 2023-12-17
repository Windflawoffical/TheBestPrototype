package com.example.thebestprototype.Screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thebestprototype.API.UserAPI;
import com.example.thebestprototype.Model.User;
import com.example.thebestprototype.R;
import com.example.thebestprototype.API.RetrofitService;
import com.example.thebestprototype.databinding.FragmentSigninBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInFragment extends Fragment {

    FragmentSigninBinding fragmentLoginBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLoginBinding = FragmentSigninBinding.inflate(inflater, container, false);
        return fragmentLoginBinding.getRoot();

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentLoginBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getActivity().getSharedPreferences("Checkbox", Context.MODE_PRIVATE);
        boolean aBoolean = preferences.getBoolean("Remember", false);
        if(aBoolean){
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_dataFragment);
        } else {
            Toast.makeText(getActivity(), "Пожалуйста, войдите в свой аккаунт!", Toast.LENGTH_SHORT).show();
        }

        fragmentLoginBinding.progressbar.setVisibility(View.GONE);
        fragmentLoginBinding.loginbutton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_dataFragment));
        fragmentLoginBinding.signuptext.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_singUpFragment));

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        fragmentLoginBinding.loginbutton.setOnClickListener(v -> {
            fragmentLoginBinding.progressbar.setVisibility(View.VISIBLE);
            String email = String.valueOf(fragmentLoginBinding.emailTextInputEditText.getText());
            String password = String.valueOf(fragmentLoginBinding.passwordTextInputEditText.getText());
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.equals("") && !password.equals("") && password.length() > 6){
                userAPI.signIn(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        Log.d("Response", "Response  =" + response);
                        if(response.isSuccessful())
                        {
                            if(fragmentLoginBinding.checkbox.isChecked()){
                                SharedPreferences sharedPreferencesforcheckbox = getActivity().getSharedPreferences("Checkbox", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferencesforcheckbox.edit();
                                editor.putBoolean("Remember", true);
                                editor.apply();
                                SharedPreferences sharedPreferencesforuseremail = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                                sharedPreferencesforuseremail.edit().putString("Useremail", response.body().getEmail()).apply();
                                Toast.makeText(getActivity(), "Авторизация успешна!\nДобро пожаловать: " + response.body().getFullname(), Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_dataFragment);
                                fragmentLoginBinding.progressbar.setVisibility(View.GONE);
                            } else {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Checkbox", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("Remember", false);
                                editor.apply();
                                SharedPreferences sharedPreferencesforuseremail = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                                sharedPreferencesforuseremail.edit().putString("Useremail", response.body().getEmail()).apply();
                                Toast.makeText(getActivity(), "Авторизация успешна!\nДобро пожаловать: " + response.body().getFullname(), Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_dataFragment);
                                fragmentLoginBinding.progressbar.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Пользователь не найден!\nПроверьте Email и Password!", Toast.LENGTH_SHORT).show();
                            fragmentLoginBinding.progressbar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        Log.d("Response", "Response  =" + t);
                        fragmentLoginBinding.progressbar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Ошибка подключения к серверу!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Заполните правильно все поля!", Toast.LENGTH_SHORT).show();
                fragmentLoginBinding.progressbar.setVisibility(View.GONE);
            }
        });
    }
}

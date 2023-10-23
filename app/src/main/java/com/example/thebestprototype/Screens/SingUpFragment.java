package com.example.thebestprototype.Screens;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.thebestprototype.API.UserAPI;
import com.example.thebestprototype.Model.User;
import com.example.thebestprototype.R;
import com.example.thebestprototype.API.RetrofitService;
import com.example.thebestprototype.databinding.FragmentSignupBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingUpFragment extends Fragment {

    FragmentSignupBinding fragmentSignupBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSignupBinding = FragmentSignupBinding.inflate(inflater, container, false);
        return fragmentSignupBinding.getRoot();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSignupBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentSignupBinding.progressbar.setVisibility(View.GONE);

        fragmentSignupBinding.signintext.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_singUpFragment_to_loginFragment));

        fragmentSignupBinding.helpbutton.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fragmentSignupBinding.getRoot().getContext());
            alertDialogBuilder.setMessage("Full name должно быть записано через пробел (Фамилия Имя).\n" +
                            "Email должен быть записан в формате email@mail.ru\n" +
                            "Password должен содержать не менее 6 символов");
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        });

        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        fragmentSignupBinding.buttonforsignup.setOnClickListener(v -> {
            fragmentSignupBinding.progressbar.setVisibility(View.VISIBLE);
            String fullname = String.valueOf(fragmentSignupBinding.fullnameTextInputEditText.getText());
            String email = String.valueOf(fragmentSignupBinding.emailTextInputEditText.getText());
            String password = String.valueOf(fragmentSignupBinding.passwordTextInputEditText.getText());
            User user = new User();
            user.setEmail(email);
            user.setFullname(fullname);
            user.setPassword(password);
            if(!fullname.equals("") && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.equals("") && !password.equals("") && password.length() > 6) {
                userAPI.signUp(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d("Response", "Response = " + response);
                        if(response.isSuccessful())
                        {
                            Toast.makeText(getActivity(), "Регистрация успешно завершена!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).navigate(R.id.action_singUpFragment_to_loginFragment);
                            fragmentSignupBinding.progressbar.setVisibility(View.GONE);
                        } else {
                            Log.d("Response", "Response = " + response);
                            Toast.makeText(getActivity(), "Пользователь уже существует!\nВыберите другой Email!", Toast.LENGTH_LONG).show();
                            fragmentSignupBinding.progressbar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("Response", "Response = " + t);
                        Toast.makeText(getActivity(), "Ошибка подключения к серверу!", Toast.LENGTH_SHORT).show();
                        fragmentSignupBinding.progressbar.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Заполните правильно все поля!", Toast.LENGTH_SHORT).show();
                fragmentSignupBinding.progressbar.setVisibility(View.GONE);
            }
        });
    }
}

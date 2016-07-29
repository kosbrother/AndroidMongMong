package com.kosbrother.mongmongwoo.login;

import com.kosbrother.mongmongwoo.entity.ResponseEntity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginPresenterTest {

    @Mock
    private LoginContract.View view;

    @Mock
    private LoginModel model;

    @Mock
    private ResponseEntity<Integer> userIdEntityResponseEntity;

    private LoginPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new LoginPresenter(view, model);
    }

    @Test
    public void testOnMmwRegisterClick() throws Exception {
        presenter.onMmwRegisterClick();

        verify(view).showRegisterDialog();
    }

    @Test
    public void testOnForgetPasswordClick() throws Exception {
        presenter.onForgetPasswordClick();

        verify(view).showForgetDialog();
    }

    @Test
    public void testOnGoogleSignInClick() throws Exception {
        presenter.onGoogleSignInClick();

        verify(view).startGoogleSignInActivityForResult();
    }

    @Test
    public void testOnFacebookLoginClick() throws Exception {
        presenter.onFacebookLoginClick();

        verify(view).startFacebookLoginActivityForResult();
    }

    @Test
    public void testOnMmwLoginClick() throws Exception {
        presenter.onMmwLoginClick();

        verify(model).checkLoginData(presenter);
    }

    @Test
    public void testOnCheckValid() throws Exception {
        presenter.onCheckValid();

        verify(view).showProgressDialog();
        verify(model).requestMmwLogin(presenter);
    }

    @Test
    public void testOnCheckError() throws Exception {
        String errorMessage = anyString();

        presenter.onCheckError(errorMessage);

        verify(view).showToast(errorMessage);
    }

    @Test
    public void testOnSignInResultOK() throws Exception {
        String email = anyString();

        presenter.onSignInResultOK(email);

        verify(view).resultOkThenFinish(email);
    }

    @Test
    public void testOnSignInResultError() throws Exception {
        String errorMessage = anyString();

        presenter.onSignInResultError(errorMessage);

        verify(view).showToast(errorMessage);
    }

    @Test
    public void testCall_loginSuccess() throws Exception {
        when(userIdEntityResponseEntity.getData()).thenReturn(1111);

        presenter.call(userIdEntityResponseEntity);

        verify(view).hideProgressDialog();
        verify(model).saveMmwUserData(userIdEntityResponseEntity.getData());
        verify(view).resultOkThenFinish(model.getEmail());
    }

    @Test
    public void testCall_loginError() throws Exception {
        when(userIdEntityResponseEntity.getData()).thenReturn(0);
        String errorMessage = "errorMessage";
        ResponseEntity.Error error = mock(ResponseEntity.Error.class);
        when(error.getMessage()).thenReturn(errorMessage);
        when(userIdEntityResponseEntity.getError()).thenReturn(error);

        presenter.call(userIdEntityResponseEntity);

        verify(view).hideProgressDialog();
        verify(view).showToast(errorMessage);
    }
}
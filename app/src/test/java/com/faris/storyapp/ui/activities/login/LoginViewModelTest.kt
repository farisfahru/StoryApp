package com.faris.storyapp.ui.activities.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.faris.storyapp.DataDummy
import com.faris.storyapp.data.network.response.LoginResponse
import com.faris.storyapp.data.repository.StoriesRepository
import com.faris.storyapp.getOrAwaitValue
import com.faris.storyapp.ui.MainDispatcherRule
import com.faris.storyapp.data.Result
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyLogin = DataDummy.generateLoginResult()
    private val dummyToken = "Bearer token27"
    private val dummyEmail = "faris@gmail.com"
    private val dummyPassword = "faris123"

    @Before
    fun setup() {
        loginViewModel = LoginViewModel(storiesRepository)
    }

    @Test
    fun `when Get Token Should Not Null and Return Success`() {
        val expectedData = MutableLiveData<String>()
        expectedData.value = dummyToken

        Mockito.`when`(storiesRepository.getSession()).thenReturn(expectedData)

        val actualToken = loginViewModel.getUserToken().getOrAwaitValue()
        assertNotNull(actualToken)
        Mockito.verify(storiesRepository).getSession()
        assertEquals(dummyToken, actualToken)
    }

    @Test
    fun `when Login Success and Return Success`()  {
        val expectedData = MutableLiveData<Result<LoginResponse>>()
        expectedData.value = Result.Success(dummyLogin)

        Mockito.`when`(storiesRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedData)

        val actualLogin = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
        assertNotNull(actualLogin)
        Mockito.verify(storiesRepository).login(dummyEmail, dummyPassword)
        assertTrue(actualLogin is Result.Success)
        assertEquals(dummyLogin, (actualLogin as Result.Success).data)
    }

    @Test
    fun `when Login Failed and Return Error`() {
        val expectedData = MutableLiveData<Result<LoginResponse>>()
        expectedData.value = Result.Error("Error")

        Mockito.`when`(storiesRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedData)

        val actualLogin = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
        assertNotNull(actualLogin)
        Mockito.verify(storiesRepository).login(dummyEmail, dummyPassword)
        assertTrue(actualLogin is Result.Error)
    }

}
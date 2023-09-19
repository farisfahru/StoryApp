package com.faris.storyapp.ui.activities.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.faris.storyapp.DataDummy
import com.faris.storyapp.data.Result
import com.faris.storyapp.data.network.response.RegisterResponse
import com.faris.storyapp.data.repository.StoriesRepository
import com.faris.storyapp.getOrAwaitValue
import com.faris.storyapp.ui.MainDispatcherRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository
    private lateinit var registerViewModel: RegisterViewModel
    private val dummyRegister = DataDummy.generateRegister()
    private val dummyName = "Faris"
    private val dummyEmail = "faris@gmail.com"
    private val dummyPassword = "faris123"

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(storiesRepository)
    }

    @Test
    fun `when Register Success and Return Success`() {
        val expectedData = MutableLiveData<Result<RegisterResponse>>()
        expectedData.value = Result.Success(dummyRegister)

        Mockito.`when`(storiesRepository.register(dummyName, dummyEmail, dummyPassword))
            .thenReturn(expectedData)

        val actualLogin =
            registerViewModel.register(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        assertNotNull(actualLogin)
        Mockito.verify(storiesRepository).register(dummyName, dummyEmail, dummyPassword)
        assertTrue(actualLogin is Result.Success)
        assertEquals(dummyRegister, (actualLogin as Result.Success).data)
    }

    @Test
    fun `when Register Failed and Return Error`() {
        val expectedData = MutableLiveData<Result<RegisterResponse>>()
        expectedData.value = Result.Error("Error")

        Mockito.`when`(storiesRepository.register(dummyName, dummyEmail, dummyPassword))
            .thenReturn(expectedData)

        val actualRegister = storiesRepository.register(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        assertNotNull(actualRegister)
        Mockito.verify(storiesRepository).register(dummyName, dummyEmail, dummyPassword)
        assertTrue(actualRegister is Result.Error)
    }
}
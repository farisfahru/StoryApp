package com.faris.storyapp.ui.activities.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import com.faris.storyapp.DataDummy
import com.faris.storyapp.data.Result
import com.faris.storyapp.data.network.response.AddStoryResponse
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

@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository
    private lateinit var addStoryViewModel: AddStoryViewModel
    private val dummyToken = "Bearer token27"
    private val dummyAddStory = DataDummy.generateAddStory()
    private val dummyMultipart = DataDummy.generateMultipart()
    private val dummyDescription = DataDummy.generateRequestBody()

    @Before
    fun setup() {
        addStoryViewModel = AddStoryViewModel(storiesRepository)
    }

    @Test
    fun `when Get Token Should Not Null and Return Success`() {
        val expectedData = MutableLiveData<String>()
        expectedData.value = dummyToken

        Mockito.`when`(storiesRepository.getSession()).thenReturn(expectedData)

        val actualToken = addStoryViewModel.getUserToken().getOrAwaitValue()
        assertNotNull(actualToken)
        Mockito.verify(storiesRepository).getSession()
        assertEquals(dummyToken, actualToken)
    }

    @Test
    fun `when Add Story Should Not Null and Return Success`() {
        val expectedData = MutableLiveData<Result<AddStoryResponse>>()
        expectedData.value = Result.Success(dummyAddStory)

        Mockito.`when`(storiesRepository.addStory(dummyToken, dummyMultipart, dummyDescription)).thenReturn(expectedData)

        val actualAddStory = addStoryViewModel.addStory(dummyToken, dummyMultipart, dummyDescription).getOrAwaitValue()
        assertNotNull(actualAddStory)
        Mockito.verify(storiesRepository).addStory(dummyToken, dummyMultipart, dummyDescription)
        assertTrue(actualAddStory is Result.Success)
    }

    @Test
    fun `when Add Story and Return Error`() {
        val expectedData = MutableLiveData<Result<AddStoryResponse>>()
        expectedData.value = Result.Error("error")

        Mockito.`when`(storiesRepository.addStory(dummyToken, dummyMultipart, dummyDescription)).thenReturn(expectedData)

        val actualAddStory = addStoryViewModel.addStory(dummyToken, dummyMultipart, dummyDescription).getOrAwaitValue()
        assertNotNull(actualAddStory)
        Mockito.verify(storiesRepository).addStory(dummyToken, dummyMultipart, dummyDescription)
        assertTrue(actualAddStory is Result.Error)
    }
}
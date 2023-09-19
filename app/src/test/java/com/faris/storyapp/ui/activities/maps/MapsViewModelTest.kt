package com.faris.storyapp.ui.activities.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.faris.storyapp.DataDummy
import com.faris.storyapp.data.Result
import com.faris.storyapp.data.network.response.StoriesResponse
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
class MapsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository
    private lateinit var mapsViewModel: MapsViewModel
    private val dummyToken = "Bearer token27"
    private val dummyStory = DataDummy.generateDummyStoriesResponse()

    @Before
    fun setup() {
        mapsViewModel = MapsViewModel(storiesRepository)
    }

    @Test
    fun `when Get Token Should Not Null and Return Success`() {
        val expectedData = MutableLiveData<String>()
        expectedData.value = dummyToken

        Mockito.`when`(storiesRepository.getSession()).thenReturn(expectedData)

        val actualToken = mapsViewModel.getUserToken().getOrAwaitValue()
        assertNotNull(actualToken)
        Mockito.verify(storiesRepository).getSession()
        assertEquals(dummyToken, actualToken)
    }

    @Test
    fun `when Get Story With Location Should Not Null and Return Data`() {
        val expectedData = MutableLiveData<Result<StoriesResponse>>()
        expectedData.value = Result.Success(dummyStory)

        Mockito.`when`(storiesRepository.getStoriesMaps(dummyToken)).thenReturn(expectedData)

        val actualData = mapsViewModel.getStoriesMaps(dummyToken).getOrAwaitValue()
        assertNotNull(actualData)
        Mockito.verify(storiesRepository).getStoriesMaps(dummyToken)
        assertTrue(actualData is Result.Success)
        assertEquals(dummyStory, (actualData as Result.Success).data)
    }

    @Test
    fun `when Get Story With Location and Return Error`() {
        val expectedData = MutableLiveData<Result<StoriesResponse>>()
        expectedData.value = Result.Error("Error")

        Mockito.`when`(storiesRepository.getStoriesMaps(dummyToken)).thenReturn(expectedData)

        val actualData = mapsViewModel.getStoriesMaps(dummyToken).getOrAwaitValue()
        assertNotNull(actualData)
        Mockito.verify(storiesRepository).getStoriesMaps(dummyToken)
        assertTrue(actualData is Result.Error)
    }
}
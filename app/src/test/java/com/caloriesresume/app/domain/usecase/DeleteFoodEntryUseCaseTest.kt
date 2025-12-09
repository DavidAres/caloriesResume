package com.caloriesresume.app.domain.usecase

import com.caloriesresume.app.domain.repository.IFoodRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class DeleteFoodEntryUseCaseTest {

    @Mock
    private lateinit var repository: IFoodRepository

    private lateinit var useCase: DeleteFoodEntryUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = DeleteFoodEntryUseCase(repository)
    }

    @Test
    fun `invoke should call repository deleteFoodEntry`() = runTest {
        val id = 1L

        useCase(id)

        verify(repository).deleteFoodEntry(id)
    }
}





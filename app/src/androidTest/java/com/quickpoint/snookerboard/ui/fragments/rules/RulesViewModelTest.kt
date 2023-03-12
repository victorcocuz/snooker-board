package com.quickpoint.snookerboard.ui.fragments.rules

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.quickpoint.snookerboard.domain.repository.GameRepository
import io.mockk.mockk
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RulesViewModelTest {
    @get:Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    private val mockRepo: GameRepository = mockk()

    private lateinit var viewModel: RulesViewModel

}
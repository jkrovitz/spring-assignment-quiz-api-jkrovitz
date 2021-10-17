package com.cooksys.quiz_api.services;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Question;

public interface QuizService {

	List<QuizResponseDto> getAllQuizzes();

	QuizResponseDto createQuiz(QuizRequestDto quizRequestDto);

    QuizResponseDto deleteQuiz(Long id);

    QuizResponseDto patchQuiz(Long id, String name);
    
    QuestionResponseDto getRandomQuestion(Long id);

    QuizResponseDto patchQuestionAdd(Long id, Question question);

    QuestionResponseDto deleteQuestion(Long id, Long questionID);


}

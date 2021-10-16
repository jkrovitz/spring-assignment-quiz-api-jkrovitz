package com.cooksys.quiz_api.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuizServiceImpl implements QuizService {

  private QuizRepository quizRepository;
  private QuizMapper quizMapper;
  
  private QuestionRepository questionRepository;
  private AnswerRepository answerRepository;

  @Override
  public List<QuizResponseDto> getAllQuizzes() {
    return quizMapper.entitiesToDtos(quizRepository.findAll());
  }
  
  @Override
  public QuizResponseDto createQuiz(QuizRequestDto quiz) {
	  Quiz quizToSave = quizRepository.saveAndFlush(quizMapper.dtoToEntity(quiz));
	  for (Question question: quizToSave.getQuestions()) {
		  question.setQuiz(quizToSave);
		  questionRepository.saveAndFlush(question);
		  for(Answer answer: question.getAnswers()) {
			  answer.setQuestion(question);		  
			  }
		  answerRepository.saveAllAndFlush(question.getAnswers());
	  }
	  
	  return quizMapper.entityToDto(quizToSave);
	
  }


}

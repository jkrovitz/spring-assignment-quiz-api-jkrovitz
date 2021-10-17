package com.cooksys.quiz_api.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.exceptions.NotFoundException;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuizServiceImpl implements QuizService {

	private final QuizRepository quizRepository;
	private final QuizMapper quizMapper;
	private final QuestionMapper questionMapper;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	private Answer getAnswer(Long id) {
		Optional<Answer> optionalAnswer = answerRepository.findById(id);
		if (optionalAnswer.isEmpty()) {
			throw new NotFoundException("No answer found with id" + id);
		}
		return optionalAnswer.get();
	}

	private Question getQuestion(Long id) {
		Optional<Question> optionalQuestion = questionRepository.findById(id);
		if (optionalQuestion.isEmpty()) {
			throw new NotFoundException("No question found with id" + id);
		}
		return optionalQuestion.get();
	}

	private Quiz getQuiz(Long id) {
		Optional<Quiz> optionalQuiz = quizRepository.findById(id);
		if (optionalQuiz.isEmpty()) {
			throw new NotFoundException("No quiz found with id " + id);
		}
		return optionalQuiz.get();
	}

	@Override
	public List<QuizResponseDto> getAllQuizzes() {
		return quizMapper.entitiesToDtos(quizRepository.findAll());
	}

	@Override
	public QuizResponseDto createQuiz(QuizRequestDto quiz) {
		Quiz quizToSave = quizRepository.saveAndFlush(quizMapper.dtoToEntity(quiz));
		for (Question question : quizToSave.getQuestions()) {
			question.setQuiz(quizToSave);
			questionRepository.saveAndFlush(question);
			for (Answer answer : question.getAnswers()) {
				answer.setQuestion(question);
			}
			answerRepository.saveAllAndFlush(question.getAnswers());
		}

		return quizMapper.entityToDto(quizToSave);

	}

	@Override
	public QuizResponseDto getQuizById(Long id) {
		// TODO Auto-generated method stub
		return quizMapper.entityToDto(getQuiz(id));
	}

	@Override
	public QuestionResponseDto getRandomQuestion(Long id) {
		Optional<Quiz> optionalQuiz = quizRepository.findById(id);
		Quiz quiz = optionalQuiz.get();
		if (optionalQuiz != null) {
			Random rand = new Random();
			int r = rand.nextInt(quiz.getQuestions().size());
			System.out.println("my quiz size is " + quiz.getQuestions().size());
			System.out.println("my random number was " + r);
			Question randomQuestion = quiz.getQuestions().get(r);
			return questionMapper.entityToDto(randomQuestion);
		}
		return null;
	}

	@Override
	public QuizResponseDto deleteQuiz(Long id) {
		Quiz quiz = getQuiz(id);
		for (Question question : quiz.getQuestions()) {
			answerRepository.deleteAll(question.getAnswers());
		}
		questionRepository.deleteAll(quiz.getQuestions());
		quizRepository.delete(quiz);
		return quizMapper.entityToDto(quiz);
	}

	@Override
	public QuizResponseDto patchQuiz(Long id, String name) {
		Optional<Quiz> quizResponse = quizRepository.findById(id);
		if (quizResponse.isPresent()) {
			Quiz quiz = quizResponse.get();
			quiz.setName(name);
			quizRepository.save(quiz);
			return quizMapper.entityToDto(quiz);
		}
		return null;
	}

	@Override
	public QuizResponseDto patchQuestionAdd(Long id, Question question) {
		Optional<Quiz> quizResponse = quizRepository.findById(id);
		if (quizResponse.isPresent()) {
			Quiz quiz = quizResponse.get();
			question.setQuiz(quiz);
			quiz.getQuestions().add(question);
			for (Answer answer : question.getAnswers()) {
				answer.setQuestion(question);
			}
			questionRepository.save(question);
			answerRepository.saveAll(question.getAnswers());
			quizRepository.save(quiz);
			return quizMapper.entityToDto(quiz);
		}
		return null;
	}

	@Override
	public QuestionResponseDto deleteQuestion(Long id, Long questionID) {
		Optional<Question> questionResponse = questionRepository.findById(questionID);
		if (questionResponse.isPresent()) {
			Question question = questionResponse.get();
			answerRepository.deleteAll(question.getAnswers());
			questionRepository.delete(question);
			return questionMapper.entityToDto(question);
		}
		return null;
	}

}

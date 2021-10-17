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
	public QuestionResponseDto getRandomQuestion(Long id) {
		Quiz quiz = getQuiz(id);
		Random rand = new Random();
		int r = rand.nextInt(quiz.getQuestions().size());
		Question randomQuestion = quiz.getQuestions().get(r);
		return questionMapper.entityToDto(randomQuestion);
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
		Quiz quiz = getQuiz(id);
		quiz.setName(name);
		quizRepository.save(quiz);
		return quizMapper.entityToDto(quiz);
	}

	@Override
	public QuizResponseDto patchQuestionAdd(Long id, Question question) {
		Quiz quiz = getQuiz(id);
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

	@Override
	public QuestionResponseDto deleteQuestion(Long id, Long questionId) {
		Quiz quiz = getQuiz(id);
		boolean question_deleted = false;
		Question q = new Question();
		for (Question question : quiz.getQuestions()) {
			if (question == questionRepository.getById(questionId)) {
				answerRepository.deleteAll(question.getAnswers());
				q = question;
				questionRepository.delete(question);
				question_deleted = true;
			}
		}
		if (question_deleted == false) {
			throw new NotFoundException("The Question with id " + questionId + " is not in quiz " + id + ".");
		}
		return questionMapper.entityToDto(q);
	}

}

package com.cooksys.quiz_api.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.entities.Question;

@Mapper(componentModel = "spring", uses = { AnswerMapper.class })
public interface QuestionMapper {

	QuestionResponseDto entityToDto(Question entity);

	Question dtoToEntity(QuestionRequestDto questionRequestDto);

	List<QuestionResponseDto> entitiesToDtos(List<Question> entities);
}

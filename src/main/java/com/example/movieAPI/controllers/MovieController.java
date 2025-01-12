package com.example.movieAPI.controllers;

import com.example.movieAPI.dtos.MovieDto;
import com.example.movieAPI.dtos.response.ApiResponse;
import com.example.movieAPI.exceptions.EmptyFileException;
import com.example.movieAPI.services.MovieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getALlMovies() {
        return ResponseEntity.ok(new ApiResponse("get all movies successfully",  movieService.getAllMovie()));
    }

    @PostMapping("/add-movie")
    public ResponseEntity<ApiResponse> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String movieDto) throws IOException, EmptyFileException {

        if (file.isEmpty()) {
            throw new EmptyFileException("File is empty! Please send another file!");
        }

        MovieDto dto = convertToMovieDto(movieDto);
        return ResponseEntity.ok(new ApiResponse("get all movies successfully",  movieService.addMovie(dto, file)));
    }

    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse> getMovieHandler(@PathVariable Integer movieId) {
        return ResponseEntity.ok(new ApiResponse("get movie successfully by Id " + movieId,  movieService.getMovie(movieId)));
    }
}

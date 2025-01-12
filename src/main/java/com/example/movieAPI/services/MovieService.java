package com.example.movieAPI.services;

import com.example.movieAPI.dtos.MovieDto;
import com.example.movieAPI.dtos.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;
    MovieDto getMovie(Integer movieId);
    List<MovieDto> getAllMovie();
    MovieDto updateMovie(Integer movieId, MultipartFile file, MovieDto updateMovie) throws IOException;
    void deleteMovie(Integer movieId) throws IOException;
    MoviePageResponse getALlMoviesWithPagination(Integer pageNumber, Integer pageSize);
    MoviePageResponse getALlMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir);
}

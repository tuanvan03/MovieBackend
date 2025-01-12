package com.example.movieAPI.services;

import com.example.movieAPI.dtos.MovieDto;
import com.example.movieAPI.entities.Movie;
import com.example.movieAPI.exceptions.FileExistException;
import com.example.movieAPI.exceptions.MovieNotFoundException;
import com.example.movieAPI.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImplement implements MovieService{
    private final MovieRepository movieRepository;
    private final FileService fileService;

    public MovieServiceImplement(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Value("${project.poster}")
    private String path;
    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // upload file
        // check file exists
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistException("File already existed");
        }
        String uploadFile = fileService.uploadFile(path, file);

        movieDto.setPoster(uploadFile);

        // map dto -> movie
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );
        Movie m = movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + uploadFile;

        // create dto response
        return new MovieDto(
                m.getMovieId(),
                m.getTitle(),
                m.getDirector(),
                m.getStudio(),
                m.getMovieCast(),
                m.getReleaseYear(),
                m.getPoster(),
                posterUrl
        );

    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // check movie exist
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie does not exist " + movieId));

        // create link posterUrl
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        // map to dto and return
        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDto> getAllMovie() {
        // Find all movie
        List<Movie> movies = movieRepository.findAll();

        // map to dto
        List<MovieDto> response = new ArrayList<>();
        for (Movie m : movies) {
            String posterUrl = baseUrl + "/file/" + m.getPoster();
            MovieDto movieDto = new MovieDto(
                    m.getMovieId(),
                    m.getTitle(),
                    m.getDirector(),
                    m.getStudio(),
                    m.getMovieCast(),
                    m.getReleaseYear(),
                    m.getPoster(),
                    posterUrl
            );
            response.add(movieDto);
        }
        // return
        return response;
    }
}

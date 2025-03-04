package com.example.movieAPI.services;

import com.example.movieAPI.dtos.MovieDto;
import com.example.movieAPI.dtos.MoviePageResponse;
import com.example.movieAPI.entities.Movie;
import com.example.movieAPI.exceptions.FileExistException;
import com.example.movieAPI.exceptions.MovieNotFoundException;
import com.example.movieAPI.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
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

    @Override
    public MovieDto updateMovie(Integer movieId, MultipartFile file, MovieDto updateMovie) throws IOException {
        // check movie exists
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie does not exist " + movieId));
        String fileName = movie.getPoster();

        // check file exists
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }
        updateMovie.setPoster(fileName);

        // update movie
        movie.setTitle(updateMovie.getTitle());
        movie.setStudio(updateMovie.getStudio());
        movie.setDirector(updateMovie.getDirector());
        movie.setMovieCast(updateMovie.getMovieCast());
        movie.setReleaseYear(updateMovie.getReleaseYear());
        movie.setPoster(updateMovie.getPoster());
        movieRepository.save(movie);

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
    public void deleteMovie(Integer movieId) throws IOException {
        // check movie exits
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie does not exist " + movieId));

        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

        movieRepository.delete(movie);
    }

    @Override
    public MoviePageResponse getALlMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        // map to MovieDto obj
        for(Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }

    @Override
    public MoviePageResponse getALlMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Movie> moviePages = movieRepository.findAll(pageable);

        List<Movie> movies = moviePages.getContent();

        List<MovieDto> movieDtos = new ArrayList<>();

        for(Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                moviePages.getTotalElements(),
                moviePages.getTotalPages(),
                moviePages.isLast());
    }
}

package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validate.ValidDateFilm;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;
    @NotEmpty
    private String name;
    @Size(max = 200, message = "Длина описания не должна быть больше 200.")
    private String description;
    @ValidDateFilm
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive
    @Max(value = 6500, message = "должно быть не больше 6500 минут.")
    private Integer duration;
    @Builder.Default
    private Map<Long, Integer> usersWhoLike = new HashMap<>();
    @Builder.Default
    private Set<Director> directors = new HashSet<>();
    private Float rate;
    private Mpa mpa;
    @Builder.Default
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

    public Long sumLikes() {
        return (long) usersWhoLike.size();
    }
}

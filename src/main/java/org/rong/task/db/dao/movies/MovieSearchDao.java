package org.rong.task.db.dao.movies;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Select;
import org.rong.task.db.model.movies.MovieSearch;

public interface MovieSearchDao {
	@Select("select movie_title,search_term from movie_search")
	public ArrayList<MovieSearch> getAll();
}

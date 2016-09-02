package org.rong.task.db.model.movies;

public class MovieSearch {
	private int id;
	private String movie_title;
	private String search_term;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMovie_title() {
		return movie_title;
	}

	public void setMovie_title(String movie_title) {
		this.movie_title = movie_title;
	}

	public String getSearch_term() {
		return search_term;
	}

	public void setSearch_term(String search_term) {
		this.search_term = search_term;
	}

	@Override
	public String toString() {
		return "MovieSearch [movie_title=" + movie_title + ", search_term="
				+ search_term + "]";
	}
}

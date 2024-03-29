package it.cgmconsulting.myblog.repository;

import it.cgmconsulting.myblog.entity.Rating;
import it.cgmconsulting.myblog.entity.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository: par1 è il nome della classe di riferimento e
@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {
}

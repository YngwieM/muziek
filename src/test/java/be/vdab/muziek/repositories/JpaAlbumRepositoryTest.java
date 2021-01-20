package be.vdab.muziek.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAlbumRepository.class)
@Sql({"/insertArtiest.sql", "/insertAlbum.sql"})
class JpaAlbumRepositoryTest
        extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String ALBUMS = "albums";
    private final JpaAlbumRepository repository;
    private final EntityManager manager;

    public JpaAlbumRepositoryTest(JpaAlbumRepository repository, EntityManager manager) {
        this.repository = repository;
        this.manager = manager;
    }

    private long idVanTestAlbum() {
        return super.jdbcTemplate.queryForObject(
                "select id from albums where naam='test'", Long.class);
    }
    @Test
    void findById() {
        var album = repository.findById(idVanTestAlbum()).get();
        assertThat(album.getNaam()).isEqualTo("test");
        assertThat(album.getArtiest().getNaam()).isEqualTo("test");
        assertThat(album.getTijd()).isEqualTo(LocalTime.of(0, 10));
    }
    @Test
    void findByOnbestaandeId() {
        assertThat(repository.findById(-1)).isNotPresent();
    }
    @Test
    void findAll() {
        var albums = repository.findAll();
        manager.clear();
        assertThat(albums).hasSize(super.countRowsInTable(ALBUMS))
                .extracting(album -> album.getNaam().toLowerCase()).isSorted();
        assertThat(albums).extracting(album -> album.getArtiest().getNaam());
    }
}
package com.clashofserres.cinematch.service;

import com.clashofserres.cinematch.data.dto.TmdbCastMemberDTO;
import com.clashofserres.cinematch.data.dto.TmdbPersonDTO;
import com.clashofserres.cinematch.data.model.ActorEntity;
import com.clashofserres.cinematch.repository.ActorRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ActorService {
    private final ActorRepository actorRepository;

    public ActorService(final ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public Optional<ActorEntity> findActorById(Long id) {
        return actorRepository.findById(id);
    }

    public Set<ActorEntity> addOrGetActorsFromCastDTOList(List<TmdbCastMemberDTO> castDTOList) {
        Set<ActorEntity> actors = new HashSet<>();
        for (TmdbCastMemberDTO castDTO : castDTOList) {
            Optional<ActorEntity> actor =addOrGetActorFromCastDTO(castDTO, false);
            if (actor.isPresent()) {
                actors.add(actor.get());
            }
        }
        actorRepository.saveAll(actors);
        return actors;
    }
    public Optional<ActorEntity> addOrGetActorFromCastDTO(TmdbCastMemberDTO castMemberDTO, boolean save) {
        try {
            Optional<ActorEntity> actor = findActorById(castMemberDTO.id());
            if (actor.isPresent()) {
                return actor;
            }
            else {
                ActorEntity newActor = actorFromDTO(castMemberDTO);
                if (save)
                    actorRepository.save(newActor);
                return Optional.of(newActor);
            }
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    private ActorEntity actorFromDTO(TmdbCastMemberDTO castMemberDTO) {
        ActorEntity actor = new ActorEntity();
        actor.setId(castMemberDTO.id());
        actor.setName(castMemberDTO.name());
        return actor;
    }
}

package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository repository) {
        this.playerRepository = repository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public boolean isExist(Long id) {
        return playerRepository.existsById(id);
    }

    public Player getPlayer(Long id) {
        return playerRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public void deletePlayer(Player player) {
        playerRepository.delete(player);
    }

    public void savePlayer(Player player) {
        playerRepository.save(player);
    }
}

package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService service) {
        this.playerService = service;
    }

    @GetMapping("rest/players")
    public ResponseEntity<List<Player>> getAllPlayers(@RequestParam(value = "name", required = false) String name,
                                                      @RequestParam(value = "title", required = false) String title,
                                                      @RequestParam(value = "race", required = false) Race race,
                                                      @RequestParam(value = "profession", required = false) Profession profession,
                                                      @RequestParam(value = "after", required = false) Long after,
                                                      @RequestParam(value = "before", required = false) Long before,
                                                      @RequestParam(value = "banned", required = false) Boolean banned,
                                                      @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                      @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                      @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                      @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                                      @RequestParam(value = "order", required = false) PlayerOrder order,
                                                      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        if (order == null){
            order = PlayerOrder.ID;
        }
        List<Player> playerList = playerService.getAllPlayers();
        playerList = playerList.stream().sorted(new Player.PlayerComparator(order)).
                filter(player -> name == null || player.getName().contains(name)).
                filter(player -> title == null || player.getTitle().contains(title)).
                filter(player -> race == null || player.getRace().equals(race)).
                filter(player -> profession == null || player.getProfession().equals(profession)).
                filter(player -> after == null || player.getBirthday().getTime() >= after).
                filter(player -> before == null || player.getBirthday().getTime() <= before).
                filter(player -> banned == null || player.getBanned() == banned).
                filter(player -> minExperience == null || player.getExperience() >= minExperience).
                filter(player -> maxExperience == null || player.getExperience() <= maxExperience).
                filter(player -> minLevel == null || player.getLevel() >= minLevel).
                filter(player -> maxLevel == null || player.getLevel() <= maxLevel).skip((long) pageNumber * pageSize).
                limit(pageSize).collect(Collectors.toList());
        return new ResponseEntity<>(playerList, HttpStatus.OK);
    }

    @GetMapping("rest/players/count")
    public ResponseEntity<Integer> countPlayers(@RequestParam(value = "name", required = false) String name,
                                                @RequestParam(value = "title", required = false) String title,
                                                @RequestParam(value = "race", required = false) Race race,
                                                @RequestParam(value = "profession", required = false) Profession profession,
                                                @RequestParam(value = "after", required = false) Long after,
                                                @RequestParam(value = "before", required = false) Long before,
                                                @RequestParam(value = "banned", required = false) Boolean banned,
                                                @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {
        Integer count = (int) playerService.getAllPlayers().stream().
                filter(player -> name == null || player.getName().contains(name)).
                filter(player -> title == null || player.getTitle().contains(title)).
                filter(player -> race == null || player.getRace().equals(race)).
                filter(player -> profession == null || player.getProfession().equals(profession)).
                filter(player -> after == null || player.getBirthday().getTime() >= after).
                filter(player -> before == null || player.getBirthday().getTime() <= before).
                filter(player -> banned == null || player.getBanned() == banned).
                filter(player -> minExperience == null || player.getExperience() >= minExperience).
                filter(player -> maxExperience == null || player.getExperience() <= maxExperience).
                filter(player -> minLevel == null || player.getLevel() >= minLevel).
                filter(player -> maxLevel == null || player.getLevel() <= maxLevel).count();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("rest/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        if (player.isValid()) {
            player.setLevelAndUntilLevel();
            if (!player.getBanned()) {
                player.setBanned(false);
            }
            playerService.savePlayer(player);
            return new ResponseEntity<>(player, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("rest/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!playerService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Player player = playerService.getPlayer(id);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping("rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@RequestBody Player player, @PathVariable("id") Long id) {
        if (id <= 0 || !player.isValidToUpdate())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!playerService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Player currentPlayer = playerService.getPlayer(id);
        if (player.getName() != null ) {
            currentPlayer.setName(player.getName());
        }
        if (player.getTitle() != null) {
            currentPlayer.setTitle(player.getTitle());
        }
        if (player.getRace() != null) {
            currentPlayer.setRace(player.getRace());
        }
        if (player.getProfession() != null) {
            currentPlayer.setProfession(player.getProfession());
        }
        if (player.getBirthday() != null) {
            currentPlayer.setBirthday(player.getBirthday());
        }
        if (player.getBanned() != null) {
            currentPlayer.setBanned(player.getBanned());
        }
        if (player.getExperience() != null) {
            currentPlayer.setExperience(player.getExperience());
            currentPlayer.setLevelAndUntilLevel();
        }
        playerService.savePlayer(currentPlayer);
        return new ResponseEntity<>(currentPlayer, HttpStatus.OK);
    }

    @DeleteMapping("rest/players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") Long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!playerService.isExist(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Player player = playerService.getPlayer(id);
        playerService.deletePlayer(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }
}

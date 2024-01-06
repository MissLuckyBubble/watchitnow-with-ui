package com.example.rest;

import com.example.dto.BaseDTO;
import com.example.dto.PlatformDTO;
import com.example.models.Platform;
import com.example.services.PlatformService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/platform")
@RestController
public class PlatformController {
    private PlatformService platformService;
    private final ModelMapper modelMapper;

    @Autowired
    private PlatformController(PlatformService platformService, ModelMapper modelMapper ){
        this.modelMapper = modelMapper;
        this.platformService = platformService;
    }

    @GetMapping()
    public List<PlatformDTO> list(){
        List<Platform> platforms = platformService.findAll();
        return  platforms
                .stream()
                .map(this::convertToPlatformDTO)
                .collect(Collectors.toList());
    }

    private PlatformDTO convertToPlatformDTO(Platform platform) {
        final PlatformDTO result = modelMapper.map(platform, PlatformDTO.class);
        return result;
    }

    @GetMapping("/{platformId}")
    public PlatformDTO getPlatform(@PathVariable(name = "platformId") long platformId) {
        Optional<Platform> optionalPlatform = platformService.getEntity(platformId);
        return optionalPlatform.map(this::convertToPlatformDTO).orElse(null);
    }

    @PostMapping()
    public BaseDTO<Platform> create(@RequestBody PlatformDTO newPlatform) {
        Platform platform = convertPlatformDTOtoModel(newPlatform);
        return convertToPlatformDTO(platformService.create(platform));
    }

    private Platform convertPlatformDTOtoModel(PlatformDTO platformDTO) {
        Platform platform = modelMapper.map(platformDTO, Platform.class);
        return platform;
    }

    @PutMapping()
    public BaseDTO<Platform> update(@RequestBody PlatformDTO updatedPlatform) {
        Platform platform = convertPlatformDTOtoModel(updatedPlatform);
        return convertToPlatformDTO(platformService.update(platform));
    }

    @DeleteMapping("/{platformId}")
    public ResponseEntity<String> delete(@PathVariable long platformId) {
        boolean isRemoved = platformService.remove(platformId);
        String deletedMessage = "Platform with id: " + platformId + " was deleted";
        String notDeletedMessage = "Platform with id: " + platformId + " does not exist";
        return isRemoved ? new ResponseEntity(deletedMessage, HttpStatusCode.valueOf(200)) :
                new ResponseEntity<>(notDeletedMessage, HttpStatusCode.valueOf(404));
    }

}

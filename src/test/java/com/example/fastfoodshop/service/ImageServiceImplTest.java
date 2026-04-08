package com.example.fastfoodshop.service;

import com.example.fastfoodshop.entity.Image;
import com.example.fastfoodshop.entity.User;
import com.example.fastfoodshop.exception.image.FileNotFoundException;
import com.example.fastfoodshop.exception.image.ImageNotFoundException;
import com.example.fastfoodshop.factory.image.ImageCreateRequestFactory;
import com.example.fastfoodshop.factory.image.ImageFactory;
import com.example.fastfoodshop.factory.user.UserFactory;
import com.example.fastfoodshop.repository.ImageRepository;
import com.example.fastfoodshop.request.ImageCreateRequest;
import com.example.fastfoodshop.response.image.ImageUpdateResponse;
import com.example.fastfoodshop.service.implementation.ImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {
    @Mock
    CloudinaryService cloudinaryService;

    @Mock
    UserService userService;

    @Mock
    ImageRepository imageRepository;

    @InjectMocks
    ImageServiceImpl imageService;

    private static final Long IMAGE_ID = 555L;

    @Test
    void uploadImage_withImageFile_shouldReturnImageUpdateResponse() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        ImageCreateRequest validRequest = ImageCreateRequestFactory.createValidWithImageFile();

        Image image = ImageFactory.createValidImage(user, IMAGE_ID);

        when(cloudinaryService.uploadImage(any(MultipartFile.class), anyString()))
                .thenReturn(new HashMap<>());

        when(imageRepository.save(any(Image.class))).thenReturn(image);

        ImageUpdateResponse imageUpdateResponse = imageService.uploadImage(user.getPhone(), validRequest);

        assertNotNull(imageUpdateResponse);
        assertNotNull(imageUpdateResponse.message());

        assertEquals(user.getId(), image.getUser().getId());

        verify(userService).findUserOrThrow(user.getPhone());
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void uploadImage_withoutImageFile_shouldThrowFileNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        ImageCreateRequest request = ImageCreateRequestFactory.createValidWithoutFile();

        assertThrows(
                FileNotFoundException.class,
                () -> imageService.uploadImage(user.getPhone(), request)
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void uploadImage_withEmptyFile_shouldThrowFileNotFoundException() {
        User user = UserFactory.createActivatedUser();

        when(userService.findUserOrThrow(user.getPhone())).thenReturn(user);

        ImageCreateRequest request = ImageCreateRequestFactory.createValidWithEmptyFile();

        assertThrows(
                FileNotFoundException.class,
                () -> imageService.uploadImage(user.getPhone(), request)
        );

        verify(userService).findUserOrThrow(user.getPhone());
    }

    @Test
    void deleteImage_existingImage_shouldReturnImageUpdateResponse() {
        User user = UserFactory.createActivatedUser();

        Image image = ImageFactory.createValidImage(user, IMAGE_ID);

        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));

        when(cloudinaryService.deleteImage(image.getPublicId())).thenReturn(true);

        doNothing().when(imageRepository).delete(image);

        ImageUpdateResponse imageUpdateResponse = imageService.deleteImage(image.getId());

        assertNotNull(imageUpdateResponse);

        verify(imageRepository).findById(image.getId());
        verify(cloudinaryService).deleteImage(image.getPublicId());
        verify(imageRepository).delete(image);
    }

    @Test
    void deleteImage_notFoundImage_shouldThrowImageNotFoundException() {
        when(imageRepository.findById(IMAGE_ID)).thenReturn(Optional.empty());

        assertThrows(ImageNotFoundException.class, () -> imageService.deleteImage(IMAGE_ID));

        verify(imageRepository).findById(IMAGE_ID);
    }
}
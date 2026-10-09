package com.hirehub;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hirehub.exception.InvalidUserIdException;
import com.hirehub.repository.UserRepository;
import com.hirehub.service.UserService;

@ExtendWith(MockitoExtension.class)


public class UserServiceTest {

	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private UserService userService;
	

	@Test
	void invalidInputTest() {
		assertThrows(InvalidUserIdException.class, () -> userService.getUserById(-1L));

        // 2. Test ID 0 with assertThrows
        assertThrows(InvalidUserIdException.class, () -> userService.getUserById(0L));

        // 3. Verify userRepository.findById(...) was never called
        verify(userRepository, never()).findById(anyLong());
        
	}

		
}

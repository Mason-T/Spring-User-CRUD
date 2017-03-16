/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.users.web;

import example.users.User;
import example.users.UserForm;
import example.users.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;

import com.querydsl.core.types.Predicate;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

/**
 * Controller to handle web requests for {@link User}s.
 * 
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @author Mason Traylor
 */

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
class UserController {

	private final UserRepository repository;

	@GetMapping("/")
	String index(Model model, //
			@QuerydslPredicate(root = User.class) Predicate predicate, //
			@PageableDefault(sort = { "lastname", "firstname" }) Pageable pageable, //
			@RequestParam MultiValueMap<String, String> parameters,
			UserForm userForm) {

		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
		builder.replaceQueryParam("page", new Object[0]);

		model.addAttribute("baseUri", builder.build().toUri());
		model.addAttribute("users", repository.findAll(predicate, pageable));

		return "index";
	}

	@PutMapping("/")
	@ResponseBody Object putUser(Model model, @Valid UserForm userForm, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
            model.addAttribute("statusMessageKey", "msg.failure");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
		}

        User user = userForm.makeUser();
        repository.delete(user.getId());
        repository.save(user);

        model.addAttribute("statusMessageKey", "msg.success");
        return new RedirectView("/");
	}

	@PostMapping("/")
	@ResponseBody Object checkUserAndCreate(Model model, @Valid UserForm userForm, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
            model.addAttribute("statusMssageKey", "msg.failure");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
		}

        User user = userForm.makeUser();
        repository.save(userForm.makeUser());
        model.addAttribute("statusMessageKey", "msg.success");
        return new RedirectView("/");
	}

	@DeleteMapping("/{userid}")
	@ResponseBody Object deleteUser(Model model, @PathVariable("userid") Long userid) {

		if (!repository.exists(userid)) {
			model.addAttribute("statusMessageKey", "msg.failure");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User id does not exist");
		}
        repository.delete(userid);
        model.addAttribute("statusMessageKey", "msg.success");
		return new RedirectView("/");
	}
}

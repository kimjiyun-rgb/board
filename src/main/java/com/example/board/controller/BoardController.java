package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import com.example.board.model.Board;
import com.example.board.model.Comment;
import com.example.board.model.User;
import com.example.board.repository.BoardRepository;
import com.example.board.repository.CommentRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class BoardController {
	@Autowired
	BoardRepository boardRepository;

	@Autowired
	HttpSession session;

	@Autowired
	CommentRepository commentRepository;

	@PostMapping("/board/comment/add")
	public String commentAdd(@ModelAttribute Comment comment, @RequestParam long boardId) {
		// 1. board_id
		Board board = new Board();
		board.setId(boardId); // 댓글을 추가할 게시물의 번호로 Board 객체 생성
		comment.setBoard(board);
		// 2. user_id
		User user = (User) session.getAttribute("user_info");
		comment.setUser(user);

		commentRepository.save(comment);
		return "redirect:/board/list"; // 임시, 에러가 보이지 않도록
	}

	@GetMapping("/board/delete/{id}")
	public String boardDelete(@PathVariable("id") long id) {
		boardRepository.deleteById(id);
		return "redirect:/board/list";
	}

	@GetMapping("/board/update/{id}")
	public String boardUpdate(Model model, @PathVariable("id") long id) {
		Optional<Board> data = boardRepository.findById(id);
		Board board = data.get();
		model.addAttribute("board", board);
		return "board/update";
	}

	@PostMapping("/board/update/{id}")
	public String boardUpdate(
			@ModelAttribute Board board, @PathVariable("id") long id) {
		User user = (User) session.getAttribute("user_info");
		String userId = user.getEmail();
		board.setUser(user);
		board.setId(id);
		boardRepository.save(board);
		return "redirect:/board/" + id;
	}

	@GetMapping("/board/{id}")
	public String boardView(Model model, @PathVariable("id") long id) {
		Optional<Board> data = boardRepository.findById(id);
		Board board = data.get();
		model.addAttribute("board", board);
		return "board/view";
	}

	@GetMapping("/board/list")
	public String boardList(Model model) {
		// Sort sort = Sort.by(Sort.Direction.DESC, "id");
		// List<Board> list = boardRepository.findAll(sort);

		Sort sort = Sort.by(Order.desc("id"));
		List<Board> list = boardRepository.findAll(sort);

		model.addAttribute("list", list);
		return "board/list";
	}

	@GetMapping("/board/write")
	public String boardWrite() {
		return "board/write";
	}

	@PostMapping("/board/write")
	public String boardWrite(@ModelAttribute Board board) {
		User user = (User) session.getAttribute("user_info");
		board.setUser(user);
		boardRepository.save(board);

		return "board/write";
	}
}
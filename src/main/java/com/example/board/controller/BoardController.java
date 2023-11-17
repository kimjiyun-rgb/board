package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.example.board.model.AtchFile;
import com.example.board.model.Board;
import com.example.board.model.Comment;
import com.example.board.model.User;
import com.example.board.repository.AtchFileRepository;
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

	@Autowired
	AtchFileRepository atchFileRepository;

	@GetMapping("/board/comment/remove")
	public String commentRemove(
			@RequestParam long id) {

		commentRepository.deleteById(id);

		return "redirect:/board/list";
	}

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
		return "redirect:/board/" + boardId; // 임시, 에러가 보이지 않도록
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
	public String boardList(
			Model model,
			@RequestParam(defaultValue = "1") int page) {
		Sort sort = Sort.by(Order.desc("id"));
		Pageable pageable = PageRequest.of(page - 1, 10, sort);
		Page<Board> list = boardRepository.findAll(pageable);

		int totalPage = list.getTotalPages();
		int start = (page - 1) / 10 * 10 + 1;
		int end = start + 9;
		// 10 2
		if (end > totalPage) {
			end = totalPage;
		}

		model.addAttribute("list", list);
		model.addAttribute("start", start);
		model.addAttribute("end", end);
		return "board/list";
	}

	@GetMapping("/board/write")
	public String boardWrite() {
		return "board/write";
	}

	@PostMapping("/board/write")
	public String boardWrite(
			@ModelAttribute Board board,
			@RequestParam("file") MultipartFile mFile) {
		/* Board 데이터 입력 - 게시글 쓰기 */
		User user = (User) session.getAttribute("user_info");
		board.setUser(user);
		Board savedBoard = boardRepository.save(board);

		/* AtchFile 데이터 입력 - 파일 첨부 */
		// 1. 파일 저장 transferTo()
		String oName = mFile.getOriginalFilename();
		try {
			mFile.transferTo(new File("c:/springboot/" + oName));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 2. 파일이 저장된 위치와 파일이름 데이터베이스에 입력
		AtchFile atchFile = new AtchFile();
		atchFile.setFilePath("c:/springboot/" + oName);
		atchFile.setBoard(savedBoard);
		atchFileRepository.save(atchFile);

		return "board/write";
	}
}
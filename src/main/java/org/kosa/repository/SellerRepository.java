package org.kosa.repository;

import org.kosa.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    List<Seller> getSellerList();
}


/*

package com.spring.jpa06.repository;

import com.spring.jpa06.entity.Board;
import com.spring.jpa06.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 1. 모든 게시글 정보 받아오기... 작성자 정보도 함께
    // findAll() + join fetch --> getBoardList()

    @Query(value="select b,m from Board b join b.member m")
    List<Board> getBoardList();

    // 2. 특정한 작성자가 쓴 게시글 정보 받아오기 ...id(kosa, jpa, kim)
    // getBoard(String id)

    //@Query(value="select b from Board b where b.member.id= :id")
    @Query(value="select b from Board b join fetch b.member m where m.id = :id")
    List<Board> getBoard(String id);

    // 3. 한강작가가 작성한 모든 게시글 받아오기...
    @Query(value="select b from Board b join fetch b.member m where m.name = :name")
    List<Board> getBoardName(String name);

    // 3-1. 파라미터값이 객체일경우**
    @Query(value = "select b from Board b join fetch b.member m where m.name = :#{#mem.name}")
    List<Board> getBoard2(Member mem);

}

 */
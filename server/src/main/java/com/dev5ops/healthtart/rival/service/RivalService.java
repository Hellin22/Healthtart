package com.dev5ops.healthtart.rival.service;

import com.dev5ops.healthtart.rival.domain.dto.RivalDTO;
import com.dev5ops.healthtart.rival.domain.dto.RivalUserInbodyDTO;

import java.util.List;

public interface RivalService {

    // 1. 내 라이벌 조회
    RivalDTO findRivalMatch();

    // 2. 선택한 라이벌 조회 -> 내꺼하고 상대꺼 2개 보여줘야함. -> 결국 유저 정보가 필요하구나? user를 infra로 가져와야한다.
    List<RivalUserInbodyDTO> findRival(String rivalUserCode);

    // 3. 라이벌 삭제 -> 라이벌 수정은 필요없을거같음. 라이벌 리스트에서 오른쪽에 삭제 버튼 만들어놓고 그걸 누르면 삭제되게 하는 로직으로 가자.
    // 그리고 삭제를 한다는게 flag를 바꾸는게 아닌거같음. user에만 flag를 만들어놓고 진행? -> 일단 하자.
    void deleteRival(Long rivalMatchCode);

    // 4. 라이벌 등록 -> 랭킹 페이지에서 상세 페이지에 들어가면 "라이벌 등록"이 있으면 괜찮을듯? -> 결국 랭킹에서 userCode가 있을거임.(보이진 않지만) 그걸 토대로 하기?
    RivalDTO insertRival(String rivalUserCode);
}

package kr.or.ddit.codes.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import kr.or.ddit.codes.repository.CodesDao;
import kr.or.ddit.common.model.CodesVo;

@Service("codesService")
public class CodesServiceImpl implements CodesService {

	@Resource(name = "codesDao")
	private CodesDao codesDao;

	public CodesServiceImpl() {
	}

	// 모든 코드정보 조회 03/06 (경찬)
	@Override
	public CodesVo allCodes() {
		return codesDao.allCodes();
	}

}
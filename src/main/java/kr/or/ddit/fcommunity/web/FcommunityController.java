package kr.or.ddit.fcommunity.web;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import kr.or.ddit.common.model.FilesVo;
import kr.or.ddit.farm.model.MarketFilesVo;
import kr.or.ddit.farm.model.MiniMarketVo;
import kr.or.ddit.fcommunity.service.FcommunityServiceImpl;
import kr.or.ddit.fsurpport.service.FsurpportServiceImpl;

// 20210318_ggy : 미니장터 controller 생성
@RequestMapping("fcommunity")
@Controller
public class FcommunityController {
	
	private static final Logger logger = LoggerFactory.getLogger(FcommunityController.class);
	
	@Resource(name = "fcommunityService")
	private FcommunityServiceImpl fcommunityService;
	
	@Resource(name = "fsurpportService")
	private FsurpportServiceImpl fsurpportService;
	
	// 20210318_ggy : 미니장터 조회
	@RequestMapping("miniMarketView")
	public String miniMarketView(Model model) {
		
		logger.debug("IN miniMarketView()");
		
		model.addAttribute("miniMarketList", fcommunityService.selectAllMiniMarketList());
		
		return "tiles.fcommunity.miniMarketMain";
	}
	
	// 20210318_ggy : 미니장터 상세 조회
	@RequestMapping("miniMarketInfoView")
	public String miniMarketInfoView( String writer, int market_no, Model model) {
		
		logger.debug("selectMiniMarketInfo 진입");
		
		MiniMarketVo miniMarketVo = new MiniMarketVo();
		miniMarketVo.setMarket_no(market_no);
		miniMarketVo.setWriter(writer);
		
		model.addAttribute("miniMarketInfo", fcommunityService.miniMarketInfo(miniMarketVo));
		
		return "tiles.fcommunity.miniMarketInfo";
	}
	
	// 20210318_ggy : 미니장터 게시글 등록을 위한 진입
	@RequestMapping("registMiniMarketView")
	public String registMiniMarketView(Model model) {

		logger.debug("registMiniMarketView 진입");
		
		model.addAttribute("miniMarketList", fcommunityService.selectMiniMarketList());
		model.addAttribute("itemList", fcommunityService.selectItemList());
		
		return "tiles.fcommunity.registMiniMarket";
	}
	
	// 20210318_ggy : 미니장터 게시글 등록을 위한 진입
	@RequestMapping(path = "registMiniMarket", method = { RequestMethod.POST })
	public String registMiniMarket(
			HttpServletRequest req, 
			MultipartFile thumbnail_file, 
			MultipartFile file_file1, 
			MultipartFile file_file2, 
			MultipartFile file_file3, 
			Model model) {
		
		logger.debug("registMiniMarket 진입");
		
		MiniMarketVo miniMarketVo = new MiniMarketVo();
		
		miniMarketVo.setWriter(req.getParameter("writer"));
		miniMarketVo.setTitle(req.getParameter("title"));
		miniMarketVo.setMobile(req.getParameter("mobile"));
		miniMarketVo.setHead_code(req.getParameter("head_code"));
		miniMarketVo.setItem_code(req.getParameter("item_code"));
		miniMarketVo.setContent(req.getParameter("content"));
		miniMarketVo.setPrice(req.getParameter("price"));
		
		
		FilesVo filesVo = new FilesVo();
		
		
		// 썸네일 파일 등록
		if (thumbnail_file.getSize() > 0) {

			logger.debug("file 있다.");

			String path = "c:\\fdown\\miniMarket\\";

			try {

				thumbnail_file.transferTo(new File(path + thumbnail_file.getOriginalFilename()));

				filesVo.setFile_nm("");
				filesVo.setFile_nm(thumbnail_file.getOriginalFilename());
				filesVo.setFile_path(path + filesVo.getFile_nm());
				} catch (IllegalStateException | IOException e) {
					miniMarketVo.setThumbnail(0);
			}

			int registFilesCnt = fsurpportService.registFiles(filesVo);

			logger.debug("registFilesCnt : " + registFilesCnt);
			logger.debug("registFilesCnt 결과값 문자 확인 : "+Integer.toString(registFilesCnt));
			
			miniMarketVo.setThumbnail(registFilesCnt);
			

		} else {
			logger.debug("파일없다.");
			miniMarketVo.setThumbnail(0);
		}
		
		
		// 게시글 등록 후 게시글 번호 가져오기
		int registPostCnt = fcommunityService.registMiniMarketPost(miniMarketVo);
		
		MarketFilesVo marketFilesVo = new MarketFilesVo();
		
		// 첨부파일 등록 부분 
		if (file_file1.getSize() > 0) {

			logger.debug("file1 있다.");

			String path = "c:\\fdown\\miniMarket\\";

			try {

				file_file1.transferTo(new File(path + file_file1.getOriginalFilename()));

				filesVo.setFile_nm("");
				filesVo.setFile_nm(file_file1.getOriginalFilename());
				filesVo.setFile_path(path + filesVo.getFile_nm());
				
				int registFilesCnt = fsurpportService.registFiles(filesVo);
				
				logger.debug("registFilesCnt : " + registFilesCnt);
				marketFilesVo.setFile_no(registFilesCnt);
				
				marketFilesVo.setMarket_no(registPostCnt);
				
				logger.debug("등록전 값 게기글 번호 : {}, 파일 번호 : {}", marketFilesVo.getMarket_no(), marketFilesVo.getFile_no());
				int registmarketfilesCnt =  fcommunityService.registmarketfiles(marketFilesVo);
				
				logger.debug("file_file1 등록 : "+ registmarketfilesCnt);
			} catch (IllegalStateException | IOException e) {
				filesVo.setFile_nm("");
			}
			
		} else {
			logger.debug("첨부 파일없다.");
		}
		
		if (file_file2.getSize() > 0) {

			logger.debug("file2 있다.");

			String path = "c:\\fdown\\miniMarket\\";

			try {

				file_file2.transferTo(new File(path + file_file2.getOriginalFilename()));

				filesVo.setFile_nm("");
				filesVo.setFile_nm(file_file2.getOriginalFilename());
				filesVo.setFile_path(path + filesVo.getFile_nm());
				
				int registFilesCnt = fsurpportService.registFiles(filesVo);

				logger.debug("registFilesCnt : " + registFilesCnt);
				marketFilesVo.setFile_no(registFilesCnt);
				
				marketFilesVo.setMarket_no(registPostCnt);
				
				logger.debug("등록전 값 게기글 번호 : {}, 파일 번호 : {}", marketFilesVo.getMarket_no(), marketFilesVo.getFile_no());
				int registmarketfilesCnt =  fcommunityService.registmarketfiles(marketFilesVo);
				
				logger.debug("file_file2 등록 : "+ registmarketfilesCnt);
				
			} catch (IllegalStateException | IOException e) {
				filesVo.setFile_nm("");
			}

		
			
		} else {
			logger.debug("첨부 파일없다.");
		}
		
		if (file_file3.getSize() > 0) {

			logger.debug("file3 있다.");

			String path = "c:\\fdown\\miniMarket\\";

			try {

				file_file3.transferTo(new File(path + file_file3.getOriginalFilename()));

				filesVo.setFile_nm("");
				filesVo.setFile_nm(file_file3.getOriginalFilename());
				filesVo.setFile_path(path + filesVo.getFile_nm());
				
				int registFilesCnt = fsurpportService.registFiles(filesVo);

				logger.debug("registFilesCnt : " + registFilesCnt);
				marketFilesVo.setFile_no(registFilesCnt);
				
				marketFilesVo.setMarket_no(registPostCnt);
				
				logger.debug("등록전 값 게기글 번호 : {}, 파일 번호 : {}", marketFilesVo.getMarket_no(), marketFilesVo.getFile_no());
				int registmarketfilesCnt =  fcommunityService.registmarketfiles(marketFilesVo);
				
				logger.debug("file_file3 등록 : "+ registmarketfilesCnt);
				
			} catch (IllegalStateException | IOException e) {
				filesVo.setFile_nm("");
			}

			
		} else {
			logger.debug("첨부 파일없다.");
		}
		
		logger.debug("미니장터 게시글 등록 종료");
		
		return"redirect:/fcommunity/miniMarketView";
	}	
	
	
	
}

package es.udc.subasta.test.model.bidservice;

import static es.udc.subasta.model.util.GlobalNames.SPRING_CONFIG_FILE;
import static es.udc.subasta.test.util.GlobalNames.SPRING_CONFIG_TEST_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pojo.modelutil.exceptions.DuplicateInstanceException;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bid.BidBlock;
import es.udc.subasta.model.bidservice.BidService;
import es.udc.subasta.model.bidservice.DecreaseBidException;
import es.udc.subasta.model.bidservice.MinimumBidPriceException;
import es.udc.subasta.model.bidservice.OwnedProductException;
import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.category.CategoryDao;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.productservice.DatesException;
import es.udc.subasta.model.productservice.ProductService;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userservice.IncorrectPasswordException;
import es.udc.subasta.model.userservice.UserProfileDetails;
import es.udc.subasta.model.userservice.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class BidServiceTest {

	private final long NON_EXISTENT_PRODUCT_ID = -1;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private BidService bidService;

	@Autowired
	private CategoryDao categoryDao;

	@Test
	public void testCreateAndFindBid() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException,
			DecreaseBidException, OwnedProductException {

		UserProfile userProfile = null;
		UserProfile userProfile2 = null;
		UserProfile owner = null;

		Category category = null;

		Product product1 = null;

		try {
			owner = userService.registerUser("owner", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));
			
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			userProfile2 = userService
					.registerUser("user2", "userPassword",
							new UserProfileDetails("name2", "lastName2",
									"user2@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", owner.getUserProfileId(),
					category.getCategoryId());
		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		Bid bid2 = bidService.findBid(bid.getBidId());

		assertEquals(bid, bid2);

	}

	@Test(expected = InstanceNotFoundException.class)
	public void testBidNonExistentProduct() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException,
			DecreaseBidException, OwnedProductException {

		UserProfile userProfile = null;
		UserProfile userProfile2 = null;

		Category category = null;

		try {
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			userProfile2 = userService
					.registerUser("user2", "userPassword",
							new UserProfileDetails("name2", "lastName2",
									"user2@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

		} catch (DuplicateInstanceException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), NON_EXISTENT_PRODUCT_ID);

	}

	@Test
	public void testOverBid() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException,
			DecreaseBidException, OwnedProductException {

		UserProfile userProfile = null;
		UserProfile userProfile2 = null;
		UserProfile owner = null;

		Category category = null;

		Product product1 = null;

		try {
			owner = userService.registerUser("owner", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));
			
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			userProfile2 = userService
					.registerUser("user2", "userPassword",
							new UserProfileDetails("name2", "lastName2",
									"user2@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", owner.getUserProfileId(),
					category.getCategoryId());

		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		assertEquals(product1.getCurrentBid().getUser().getEmail(),
				"user@udc.es");
		assertEquals(product1.getCurrentPrice(), new BigDecimal(10.0));
		assertEquals(product1.getCurrentBid().getAmount(), new BigDecimal(10.0));

		Bid bid2 = bidService.createBid(userProfile2.getUserProfileId(),
				new BigDecimal(12), product1.getProductId());

		assertEquals(product1.getCurrentBid().getUser().getEmail(),
				"user2@udc.es");
		assertEquals(product1.getCurrentPrice(), new BigDecimal(10.5));
		assertEquals(product1.getCurrentBid().getAmount(), new BigDecimal(12.0));

	}

	@Test(expected = MinimumBidPriceException.class)
	public void testOverBidMinimumNotReached()
			throws InstanceNotFoundException, MinimumBidPriceException,
			ExpiredDateException, DecreaseBidException, OwnedProductException {

		UserProfile userProfile = null;
		UserProfile userProfile2 = null;
		UserProfile owner = null;
		
		Category category = null;

		Product product1 = null;

		try {
			owner = userService.registerUser("owner", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));
			
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			userProfile2 = userService
					.registerUser("user2", "userPassword",
							new UserProfileDetails("name2", "lastName2",
									"user2@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", owner.getUserProfileId(),
					category.getCategoryId());

		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		Bid bid2 = bidService.createBid(userProfile2.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

	}

	@Test(expected = DecreaseBidException.class)
	public void testDecreaseBid() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException,
			DecreaseBidException, OwnedProductException {

		UserProfile userProfile = null;
		UserProfile owner = null;

		Category category = null;

		Product product1 = null;

		try {
			owner = userService.registerUser("owner", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));
			
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", owner.getUserProfileId(),
					category.getCategoryId());

		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		Bid bid2 = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(1), product1.getProductId());

		assertEquals(product1.getCurrentBid().getUser().getEmail(),
				"user@udc.es");

		assertEquals(product1.getCurrentPrice(), new BigDecimal(10.0));

	}

	@Test
	public void testIncreaseBid() throws InstanceNotFoundException,
			MinimumBidPriceException, ExpiredDateException,
			DecreaseBidException, OwnedProductException {

		UserProfile userProfile = null;
		UserProfile owner = null;

		Category category = null;

		Product product1 = null;

		try {
			owner = userService.registerUser("owner", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));
			
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));

			category = new Category("Category Test");
			categoryDao.save(category);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2018);

			product1 = productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", owner.getUserProfileId(),
					category.getCategoryId());

		} catch (DuplicateInstanceException | DatesException e) {
			System.out
					.println("Something went wrong creating data for the test");
		}

		Bid bid = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());

		Bid bid2 = bidService.createBid(userProfile.getUserProfileId(),
				new BigDecimal(12), product1.getProductId());

		assertEquals(product1.getCurrentBid().getUser().getEmail(),
				"user@udc.es");

		assertEquals(product1.getCurrentPrice(), new BigDecimal(10.0));

	}

	/**
	 * QueryStateBidsByUser show the bids state by user
	 */

	@Test
	public void queryStateBidsByUser() throws DuplicateInstanceException,
			InstanceNotFoundException, IncorrectPasswordException,
			DatesException, MinimumBidPriceException, ExpiredDateException,
			DecreaseBidException, OwnedProductException {

		/* This is an array which will contain the expected result */
		List<Bid> expectedBidsByUser = new ArrayList<Bid>();

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");

		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2017);
		Calendar cal2 = Calendar.getInstance();
		cal2.set(Calendar.YEAR, 2018);

		/* Create a user who has created the Advert */
		UserProfile AdvertUser1 = createUsuario("User1", "userPassword");
		UserProfile AdverUser2 = createUsuario("User2", "userPassword");
		UserProfile otherUser = createUsuario("otherUser", "userPassword");

		/* create the profile who see the state of bids */

		UserProfile owner = userService.registerUser("owner", "userPassword",
				new UserProfileDetails("name", "lastName", "user@udc.es"));

		UserProfile authenticatedUser = userService.login(
				otherUser.getLoginName(), otherUser.getEncryptedPassword(),
				true);

		Product product1 = productService
				.insertAd("iPad Mini 3", "This is a product for the test",
						new BigDecimal(10.0), cal1, "NA",
						owner.getUserProfileId(),
						category1.getCategoryId());

		Product product2 = productService.insertAd("Apple iPhone 6 Plus",
				"This is a product for the test", new BigDecimal(10.0), cal2,
				"NA", owner.getUserProfileId(), category2.getCategoryId());

		/* owner bids */
		Bid bid1 = bidService.createBid(authenticatedUser.getUserProfileId(),
				new BigDecimal(10.0), product1.getProductId());
		expectedBidsByUser.add(bid1);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Bid bid2 = bidService.createBid(authenticatedUser.getUserProfileId(),
				new BigDecimal(12.0), product1.getProductId());
		expectedBidsByUser.add(bid2);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Bid bid3 = bidService.createBid(authenticatedUser.getUserProfileId(),
				new BigDecimal(10.0), product2.getProductId());
		expectedBidsByUser.add(bid3);

		/* Other user bids */
		bidService.createBid(AdverUser2.getUserProfileId(), new BigDecimal(
				11.00), product2.getProductId());

		bidService.createBid(AdvertUser1.getUserProfileId(), new BigDecimal(
				13.00), product1.getProductId());

		/* Sort expectedBidsByUser in DESC way */
		Collections.sort(expectedBidsByUser, BidComparator);

		/* Find bids by Owner user and check with expectedBidsByUser */
		BidBlock bidBlock;
		int count = 5;
		int startIndex = 0;
		short resultIndex = 0;

		do {
			bidBlock = bidService.findBidsByUserId(
					authenticatedUser.getUserProfileId(), startIndex, count);

			for (Bid b : bidBlock.getBids()) {
				System.out.println(b.getDate());

			}

			assertTrue(bidBlock.getBids().size() <= count);
			for (Bid bid : bidBlock.getBids()) {
				assertTrue(bid == expectedBidsByUser.get(resultIndex++));

			}
			startIndex += count;
		} while (bidBlock.getExistMoreBids());

		assertTrue(expectedBidsByUser.size() == startIndex - count
				+ bidBlock.getBids().size());

	}

	@Test
	public void queryStateBidsEmpty() throws InstanceNotFoundException,
			DuplicateInstanceException, IncorrectPasswordException,
			DatesException, MinimumBidPriceException, ExpiredDateException,
			DecreaseBidException, OwnedProductException {

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");

		Calendar endigDate1 = Calendar.getInstance();
		endigDate1.add(Calendar.MONTH, 5);

		/* Create users to create other bids */

		UserProfile advertUser1 = createUsuario("User1", "userPassword");
		UserProfile advertUser2 = createUsuario("User2", "userPassword");
		UserProfile bidUser1 = createUsuario("Userb1", "userPassword");
		UserProfile bidUser2 = createUsuario("Userb2", "userPassword");

		/* create the owner profile who see the state of bids */
		UserProfile userProfile = registerUser("owner", "userPassword");
		UserProfile authenticatedUser = userService.login(
				userProfile.getLoginName(), userProfile.getEncryptedPassword(),
				true);

		/* The bids are created by other users */
		Product product1 = productService.insertAd("iPad Mini 3",
				"This is a product for the test", new BigDecimal(10.0),
				endigDate1, "NA", advertUser1.getUserProfileId(),
				category1.getCategoryId());

		endigDate1.add(Calendar.MONTH, 3);
		endigDate1.add(Calendar.YEAR, 10);

		Product product2 = productService.insertAd("Apple iPhone 6 Plus",
				"This is a product for the test", new BigDecimal(10.0),
				endigDate1, "NA", advertUser2.getUserProfileId(),
				category2.getCategoryId());

		bidService.createBid(bidUser1.getUserProfileId(), new BigDecimal(10.0),
				product1.getProductId());

		bidService.createBid(bidUser2.getUserProfileId(), new BigDecimal(12.0),
				product1.getProductId());

		bidService.createBid(bidUser1.getUserProfileId(), new BigDecimal(10.0),
				product2.getProductId());

		bidService.createBid(bidUser2.getUserProfileId(),
				new BigDecimal(11.00), product2.getProductId());

		bidService.createBid(bidUser1.getUserProfileId(),
				new BigDecimal(13.00), product1.getProductId());

		/* The result must be empty because the bid was create by other users */

		assertEquals(
				bidService
						.findBidsByUserId(authenticatedUser.getUserProfileId(),
								0, 5).getBids().size(), 0);

	}

	private UserProfile registerUser(String loginName, String clearPassword) {

		UserProfileDetails userProfileDetails = new UserProfileDetails("name",
				"lastName", "user@udc.es");

		try {

			return userService.registerUser(loginName, clearPassword,
					userProfileDetails);

		} catch (DuplicateInstanceException e) {
			throw new RuntimeException(e);
		}

	}

	private UserProfile createUsuario(String user, String password)
			throws DuplicateInstanceException {
		return userService.registerUser(user, password, new UserProfileDetails(
				"name", "lastName", "user@udc.es"));
	}

	private Category createCategory(String typeOfCategory) {
		Category category = new Category(typeOfCategory);
		categoryDao.save(category);
		return category;

	}

	/*
	 * Order by Latest Bids ->
	 */

	public static Comparator<Bid> BidComparator = new Comparator<Bid>() {
		public int compare(Bid bid1, Bid bid2) {
			return bid2.getDate().compareTo(bid1.getDate());
		}
	};

}

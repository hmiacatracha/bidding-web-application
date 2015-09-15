package es.udc.subasta.test.model.productservice;

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
import es.udc.subasta.model.bidservice.MinimumBidPriceException;
import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.category.CategoryDao;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductBlock;
import es.udc.subasta.model.product.ProductDao;
import es.udc.subasta.model.productservice.DatesException;
import es.udc.subasta.model.productservice.ProductService;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userservice.IncorrectPasswordException;
import es.udc.subasta.model.userservice.UserProfileDetails;
import es.udc.subasta.model.userservice.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { SPRING_CONFIG_FILE, SPRING_CONFIG_TEST_FILE })
@Transactional
public class ProductServiceTest {

	private final long NON_EXISTENT_USER_PROFILE_ID = -1;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private ProductDao productDao;

	@Test
	public void testInsertAdAndFindProduct() throws DuplicateInstanceException,
			InstanceNotFoundException, DatesException {

		UserProfile userProfile = userService.registerUser("user",
				"userPassword", new UserProfileDetails("name", "lastName",
						"user@udc.es"));

		Category category = new Category("Category Test");
		categoryDao.save(category);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2018);

		Product product1 = productService.insertAd("ProductTest1",
				"This is a product for the test", new BigDecimal(10.0), cal,
				"NA", userProfile.getUserProfileId(), category.getCategoryId());

		Product product2 = productDao.find(product1.getProductId());

		assertEquals(product1, product2);

	}

	@Test(expected = DatesException.class)
	public void testInsertAdExpiredDate() throws DuplicateInstanceException,
			InstanceNotFoundException, DatesException {

		UserProfile userProfile = userService.registerUser("user",
				"userPassword", new UserProfileDetails("name", "lastName",
						"user@udc.es"));

		Category category = new Category("Category Test");
		categoryDao.save(category);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2014);

		Product product1 = productService.insertAd("ProductTest1",
				"This is a product for the test", new BigDecimal(10.0), cal,
				"NA", userProfile.getUserProfileId(), category.getCategoryId());

	}

	@Test
	public void testFindProductNoParameters()
			throws DuplicateInstanceException, InstanceNotFoundException,
			DatesException {
		/* Expect List */
		List<Product> expectedProductsNoParameters = new ArrayList<Product>();

		/*
		 * Create some data and then we are going to find them
		 */

		UserProfile usuario1 = createUsuario("User1", "userPassword");
		UserProfile usuario2 = createUsuario("User2", "userPassword");
		UserProfile usuario3 = createUsuario("User3", "userPassword");

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");
		Category category3 = createCategory("Computer");

		Calendar startingDate1 = Calendar.getInstance();
		startingDate1.set(Calendar.YEAR, 2017);
		Calendar startingDate2 = Calendar.getInstance();
		startingDate2.set(Calendar.YEAR, 2019);
		Calendar startingDate3 = Calendar.getInstance();
		startingDate3.set(Calendar.YEAR, 2018);

		Product product1 = createProduct("iPad Mini 3", "test of prueba 1",
				new BigDecimal(10.0), startingDate1, "NA",
				usuario1.getUserProfileId(), category1.getCategoryId());
		expectedProductsNoParameters.add(product1);

		Product product2 = createProduct("iPad Air 2 ", "description",
				new BigDecimal(10.0), startingDate2, "NA",
				usuario2.getUserProfileId(), category1.getCategoryId());
		expectedProductsNoParameters.add(product2);

		Product product3 = createProduct("Apple iPhone 6 Plus",
				"This is a special product", new BigDecimal(10.0),
				startingDate3, "NA", usuario3.getUserProfileId(),
				category2.getCategoryId());
		expectedProductsNoParameters.add(product3);

		Product product4 = createProduct("Samsung Galaxy Note 4",
				"This is a special product", new BigDecimal(10.0),
				startingDate3, "NA", usuario3.getUserProfileId(),
				category2.getCategoryId());
		expectedProductsNoParameters.add(product4);

		Product product5 = createProduct("Apple MacBook Air", "description",
				new BigDecimal(10.0), startingDate2, "NA",
				usuario2.getUserProfileId(), category3.getCategoryId());
		expectedProductsNoParameters.add(product5);

		Product product6 = createProduct("Toshiba Satellite E Series",
				"test of prueba 1", new BigDecimal(10.0), startingDate1, "NA",
				usuario1.getUserProfileId(), category3.getCategoryId());
		expectedProductsNoParameters.add(product6);

		/* Sort the array by endingDate */

		Collections.sort(expectedProductsNoParameters, ProductComparator);

		/*
		 * Find no parameters
		 */

		ProductBlock productBlock;
		int count = 3;
		int startIndex = 0;
		short resultIndex = 0;

		do {
			productBlock = (ProductBlock) productService
					.findProductsByKeywordsCategory(null, null, startIndex,
							count);

			assertTrue(productBlock.getProducts().size() <= count);
			for (Product product : productBlock.getProducts()) {
				assertTrue(product == expectedProductsNoParameters
						.get(resultIndex++));
			}
			startIndex += count;
		} while (productBlock.getExistMoreProducts());

		assertTrue(expectedProductsNoParameters.size() == startIndex - count
				+ productBlock.getProducts().size());

	}

	@Test
	public void testGetAllProducts() {

		UserProfile userProfile = null;
		try {
			userProfile = userService.registerUser("user", "userPassword",
					new UserProfileDetails("name", "lastName", "user@udc.es"));
		} catch (DuplicateInstanceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Category category = new Category("Category Test");
		categoryDao.save(category);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2018);

		try {
			productService.insertAd("ProductTest1",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", userProfile.getUserProfileId(),
					category.getCategoryId());

			productService.insertAd("ProductTest2",
					"This is a product for the test", new BigDecimal(10.0),
					cal, "NA", userProfile.getUserProfileId(),
					category.getCategoryId());

		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ProductBlock products = productService.findProductsByKeywordsCategory(
				null, null, 0, 10);

		List<Product> productList = products.getProducts();

		assertEquals(2, productList.size());
		assertEquals("ProductTest1", productList.get(0).getName());
		assertEquals("ProductTest2", productList.get(1).getName());

	}

	@Test
	public void testFindProductByKeywords() throws DuplicateInstanceException,
			InstanceNotFoundException, DatesException {

		/*
		 * This array will cointais the expect result of seaching product by
		 * keywords
		 */

		List<Product> expectedProductsByKeywords = new ArrayList<Product>();

		/* Create some data to check the find by keywords */
		UserProfile usuario1 = createUsuario("User1", "userPassword");
		Category category1 = createCategory("Impresora");
		Calendar endingDate = Calendar.getInstance();
		endingDate.set(Calendar.YEAR, 2017);

		Product product1 = createProduct("Canon PowerShot S50",
				"test of prueba 1", new BigDecimal(10.0), endingDate, "NA",
				usuario1.getUserProfileId(), category1.getCategoryId());
		expectedProductsByKeywords.add(product1);

		Product product2 = createProduct("Canon S50", "test of prueba 1",
				new BigDecimal(10.0), endingDate, "NA",
				usuario1.getUserProfileId(), category1.getCategoryId());
		expectedProductsByKeywords.add(product2);

		createProduct("Canon PIXMA iP2700", "description",
				new BigDecimal(10.0), endingDate, "NA",
				usuario1.getUserProfileId(), category1.getCategoryId());

		createProduct("Lexmark s50 ", "This is a special product",
				new BigDecimal(10.0), endingDate, "NA",
				usuario1.getUserProfileId(), category1.getCategoryId());

		// Sort the array by name
		Collections.sort(expectedProductsByKeywords, ProductComparator);

		// Find only by Keywords
		ProductBlock productBlock;
		int count = 3;
		int startIndex = 0;
		short resultIndex = 0;

		do {
			productBlock = (ProductBlock) productService
					.findProductsByKeywordsCategory("s50 CAN", null,
							startIndex, count);

			assertTrue(productBlock.getProducts().size() <= count);

			for (Product product : productBlock.getProducts()) {
				assertTrue(product == expectedProductsByKeywords
						.get(resultIndex++));

			}
			startIndex += count;
		} while (productBlock.getExistMoreProducts());

		assertTrue(expectedProductsByKeywords.size() == startIndex - count
				+ productBlock.getProducts().size());

	}

	@Test
	public void testFindProductByKeywordsAndCategory()
			throws DuplicateInstanceException, InstanceNotFoundException,
			DatesException {

		List<Product> expectedProductsByKwAndCat = new ArrayList<Product>();

		/*
		 * Create some data and then we are going to find them
		 */

		UserProfile ownerProduct = createUsuario("User1", "userPassword");

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");
		Calendar startingDate1 = Calendar.getInstance();
		startingDate1.set(Calendar.YEAR, 2017);

		Product find1 = createProduct("iPad Mini 3 Apple", "test of prueba 1",
				new BigDecimal(10.0), startingDate1, "NA",
				ownerProduct.getUserProfileId(), category1.getCategoryId());
		expectedProductsByKwAndCat.add(find1);

		startingDate1.add(Calendar.MONTH, 3);
		createProduct("Apple iPhone 6 Plus", "This is a special product",
				new BigDecimal(10.0), startingDate1, "NA",
				ownerProduct.getUserProfileId(), category2.getCategoryId());
		startingDate1.set(Calendar.YEAR, 2019);

		Product find2 = createProduct("Apple iPad Air 2 ", "description",
				new BigDecimal(10.0), startingDate1, "NA",
				ownerProduct.getUserProfileId(), category1.getCategoryId());
		expectedProductsByKwAndCat.add(find2);

		/* Sort the array by endingDate */

		Collections.sort(expectedProductsByKwAndCat, ProductComparator);

		/*
		 * Find no parameters
		 */

		ProductBlock productBlock;
		int count = 3;
		int startIndex = 0;
		short resultIndex = 0;

		do {
			productBlock = (ProductBlock) productService
					.findProductsByKeywordsCategory("ip apple",
							category1.getCategoryId(), startIndex, count);

			assertTrue(productBlock.getProducts().size() <= count);
			for (Product product : productBlock.getProducts()) {
				assertTrue(product == expectedProductsByKwAndCat
						.get(resultIndex++));
			}
			startIndex += count;
		} while (productBlock.getExistMoreProducts());

		assertTrue(expectedProductsByKwAndCat.size() == startIndex - count
				+ productBlock.getProducts().size());
	}

	@Test
	public void testViewProductDetails() throws DuplicateInstanceException,
			InstanceNotFoundException, DatesException {
		UserProfile ownerProduct = createUsuario("User1", "userPassword");

		Category category1 = createCategory("tablet");
		Calendar endingDate1 = Calendar.getInstance();
		endingDate1.set(Calendar.YEAR, 2017);

		Product find1 = createProduct("iPad Mini 3 Apple", "test of prueba 1",
				new BigDecimal(10.0), endingDate1, "NA",
				ownerProduct.getUserProfileId(), category1.getCategoryId());

		assertTrue(category1.getCategoryId() == find1.getCategory()
				.getCategoryId());
		assertTrue(endingDate1.getTimeInMillis() == find1.getEndingDate()
				.getTimeInMillis());
		assertTrue("iPad Mini 3 Apple".compareTo(find1.getName()) == 0);
		assertTrue("test of prueba 1".compareTo(find1.getDescription()) == 0);
		assertTrue(find1.getStartingPrice().compareTo(new BigDecimal(10.0)) == 0);
		assertTrue(ownerProduct.getUserProfileId() == find1.getOwner()
				.getUserProfileId());

	}

	@Test
	public void testListAdvertisedProducts() throws InstanceNotFoundException,
			DatesException, DuplicateInstanceException {
		List<Product> expectedAdviceProducts = new ArrayList<Product>();

		UserProfile userProfile = null;

		userProfile = userService.registerUser("user", "userPassword",
				new UserProfileDetails("name", "lastName", "user@udc.es"));

		UserProfile userProfile2 = null;

		userProfile2 = userService.registerUser("user2", "userPassword2",
				new UserProfileDetails("name2", "lastName2", "user2@udc.es"));

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");
		Category category3 = createCategory("Computer");

		Calendar endingDate1 = Calendar.getInstance();
		endingDate1.set(Calendar.YEAR, 2017);
		Calendar endingDate2 = Calendar.getInstance();
		endingDate2.set(Calendar.YEAR, 2019);
		Calendar endingDate3 = Calendar.getInstance();
		endingDate3.set(Calendar.YEAR, 2018);

		/* Create some Products by owner user */
		Product product1 = createProduct("iPad Mini 3", "test of prueba 1",
				new BigDecimal(10.0), endingDate1, "NA",
				userProfile.getUserProfileId(), category1.getCategoryId());
		expectedAdviceProducts.add(product1);

		Product product2 = createProduct("iPad Air 2 ", "description",
				new BigDecimal(10.0), endingDate2, "NA",
				userProfile.getUserProfileId(), category1.getCategoryId());
		expectedAdviceProducts.add(product2);

		Product product3 = createProduct("Apple iPhone 6 Plus",
				"This is a special product", new BigDecimal(10.0), endingDate3,
				"NA", userProfile2.getUserProfileId(),
				category2.getCategoryId());
		expectedAdviceProducts.add(product3);

		Product product4 = createProduct("Apple MacBook Air", "description",
				new BigDecimal(10.0), endingDate2, "NA",
				userProfile2.getUserProfileId(), category3.getCategoryId());
		expectedAdviceProducts.add(product4);

		List<Product> productList = productService
				.findProductsByKeywordsCategory(null, null, 0, 10)
				.getProducts();

		assertTrue(productList.size() == 4);
		assertEquals(productList.get(0), product2);
		assertEquals(productList.get(1), product4);
		assertEquals(productList.get(2), product3);
		assertEquals(productList.get(3), product1);

	}

	@Test
	public void queryAdvertisedProductsByUser()
			throws DuplicateInstanceException, InstanceNotFoundException,
			IncorrectPasswordException, DatesException {
		/* This is an array which will contain the expected result */
		List<Product> expectedAdviceProducts = new ArrayList<Product>();

		/* Create a owner user */
		UserProfile userProfile = registerUser("owner", "userPassword");
		UserProfile authenticatedUser = userService.login(
				userProfile.getLoginName(), userProfile.getEncryptedPassword(),
				true);

		Category category1 = createCategory("tablet");
		Category category2 = createCategory("Mobile");
		Category category3 = createCategory("Computer");

		Calendar endingDate1 = Calendar.getInstance();
		endingDate1.set(Calendar.YEAR, 2017);
		Calendar endingDate2 = Calendar.getInstance();
		endingDate2.set(Calendar.YEAR, 2017);
		Calendar endingDate3 = Calendar.getInstance();
		endingDate3.set(Calendar.YEAR, 2018);

		/* Create some Products by owner user */
		Product product1 = createProduct("iPad Mini 3", "test of prueba 1",
				new BigDecimal(10.0), endingDate1, "NA",
				authenticatedUser.getUserProfileId(), category1.getCategoryId());
		expectedAdviceProducts.add(product1);

		Product product2 = createProduct("iPad Air 2 ", "description",
				new BigDecimal(10.0), endingDate2, "NA",
				authenticatedUser.getUserProfileId(), category1.getCategoryId());
		expectedAdviceProducts.add(product2);

		Product product3 = createProduct("Apple iPhone 6 Plus",
				"This is a special product", new BigDecimal(10.0), endingDate3,
				"NA", authenticatedUser.getUserProfileId(),
				category2.getCategoryId());
		expectedAdviceProducts.add(product3);

		Product product4 = createProduct("Apple MacBook Air", "description",
				new BigDecimal(10.0), endingDate2, "NA",
				authenticatedUser.getUserProfileId(), category3.getCategoryId());
		expectedAdviceProducts.add(product4);

		/* Sort the array by ending date in desc way */

		Collections.sort(expectedAdviceProducts, ProductComparator);

		/* Check the result of findAdvertiseProductsByUserId */
		ProductBlock productBlock;
		int count = 4;
		int startIndex = 0;
		short resultIndex = 0;

		do {

			productBlock = productService.findAdvertisedProductsByUserId(
					authenticatedUser.getUserProfileId(), startIndex, count);

			assertTrue(productBlock.getProducts().size() <= count);

			for (Product product : productBlock.getProducts()) {
				assertTrue(product == expectedAdviceProducts.get(resultIndex++));
			}
			startIndex += count;
		} while (productBlock.getExistMoreProducts());

		assertTrue(expectedAdviceProducts.size() == startIndex - count
				+ productBlock.getProducts().size());
	}

	public void queryStateAdvertNotExistUser()
			throws InstanceNotFoundException, DuplicateInstanceException,
			IncorrectPasswordException, DatesException,
			MinimumBidPriceException, ExpiredDateException {

		productService.findAdvertisedProductsByUserId(
				NON_EXISTENT_USER_PROFILE_ID, 0, 5);

	}

	private Product createProduct(String name, String description,
			BigDecimal startingPrice, Calendar endingDate,
			String deliveryInformation, Long ownerId, Long categoryId)
			throws InstanceNotFoundException, DatesException {

		return productService.insertAd(name, description, startingPrice,
				endingDate, deliveryInformation, ownerId, categoryId);
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

	public static Comparator<Product> ProductComparator = new Comparator<Product>() {
		public int compare(Product p1, Product p2) {
			return p2.getEndingDate().compareTo(p1.getEndingDate());
		}
	};

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
}

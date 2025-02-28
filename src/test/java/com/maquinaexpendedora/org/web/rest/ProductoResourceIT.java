package com.maquinaexpendedora.org.web.rest;

import static com.maquinaexpendedora.org.domain.ProductoAsserts.*;
import static com.maquinaexpendedora.org.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maquinaexpendedora.org.IntegrationTest;
import com.maquinaexpendedora.org.domain.Producto;
import com.maquinaexpendedora.org.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductoResourceIT {

    private static final Integer DEFAULT_CLAVE = 1;
    private static final Integer UPDATED_CLAVE = 2;
    private static final Integer SMALLER_CLAVE = 1 - 1;

    private static final String DEFAULT_NOMBRE_PRODUCTO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_PRODUCTO = "BBBBBBBBBB";

    private static final Double DEFAULT_PRECIO = 1D;
    private static final Double UPDATED_PRECIO = 2D;
    private static final Double SMALLER_PRECIO = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/productos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductoMockMvc;

    private Producto producto;

    private Producto insertedProducto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producto createEntity(EntityManager em) {
        Producto producto = new Producto().clave(DEFAULT_CLAVE).nombreProducto(DEFAULT_NOMBRE_PRODUCTO).precio(DEFAULT_PRECIO);
        return producto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producto createUpdatedEntity(EntityManager em) {
        Producto producto = new Producto().clave(UPDATED_CLAVE).nombreProducto(UPDATED_NOMBRE_PRODUCTO).precio(UPDATED_PRECIO);
        return producto;
    }

    @BeforeEach
    public void initTest() {
        producto = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProducto != null) {
            productoRepository.delete(insertedProducto);
            insertedProducto = null;
        }
    }

    @Test
    @Transactional
    void createProducto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Producto
        var returnedProducto = om.readValue(
            restProductoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(producto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Producto.class
        );

        // Validate the Producto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertProductoUpdatableFieldsEquals(returnedProducto, getPersistedProducto(returnedProducto));

        insertedProducto = returnedProducto;
    }

    @Test
    @Transactional
    void createProductoWithExistingId() throws Exception {
        // Create the Producto with an existing ID
        producto.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(producto)))
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProductos() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList
        restProductoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(producto.getId().intValue())))
            .andExpect(jsonPath("$.[*].clave").value(hasItem(DEFAULT_CLAVE)))
            .andExpect(jsonPath("$.[*].nombreProducto").value(hasItem(DEFAULT_NOMBRE_PRODUCTO)))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(DEFAULT_PRECIO.doubleValue())));
    }

    @Test
    @Transactional
    void getProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get the producto
        restProductoMockMvc
            .perform(get(ENTITY_API_URL_ID, producto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(producto.getId().intValue()))
            .andExpect(jsonPath("$.clave").value(DEFAULT_CLAVE))
            .andExpect(jsonPath("$.nombreProducto").value(DEFAULT_NOMBRE_PRODUCTO))
            .andExpect(jsonPath("$.precio").value(DEFAULT_PRECIO.doubleValue()));
    }

    @Test
    @Transactional
    void getProductosByIdFiltering() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        Long id = producto.getId();

        defaultProductoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProductoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProductoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductosByClaveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where clave equals to
        defaultProductoFiltering("clave.equals=" + DEFAULT_CLAVE, "clave.equals=" + UPDATED_CLAVE);
    }

    @Test
    @Transactional
    void getAllProductosByClaveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where clave in
        defaultProductoFiltering("clave.in=" + DEFAULT_CLAVE + "," + UPDATED_CLAVE, "clave.in=" + UPDATED_CLAVE);
    }

    @Test
    @Transactional
    void getAllProductosByClaveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where clave is not null
        defaultProductoFiltering("clave.specified=true", "clave.specified=false");
    }

    @Test
    @Transactional
    void getAllProductosByClaveIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where clave is greater than or equal to
        defaultProductoFiltering("clave.greaterThanOrEqual=" + DEFAULT_CLAVE, "clave.greaterThanOrEqual=" + UPDATED_CLAVE);
    }

    @Test
    @Transactional
    void getAllProductosByClaveIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where clave is less than or equal to
        defaultProductoFiltering("clave.lessThanOrEqual=" + DEFAULT_CLAVE, "clave.lessThanOrEqual=" + SMALLER_CLAVE);
    }

    @Test
    @Transactional
    void getAllProductosByClaveIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where clave is less than
        defaultProductoFiltering("clave.lessThan=" + UPDATED_CLAVE, "clave.lessThan=" + DEFAULT_CLAVE);
    }

    @Test
    @Transactional
    void getAllProductosByClaveIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where clave is greater than
        defaultProductoFiltering("clave.greaterThan=" + SMALLER_CLAVE, "clave.greaterThan=" + DEFAULT_CLAVE);
    }

    @Test
    @Transactional
    void getAllProductosByNombreProductoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where nombreProducto equals to
        defaultProductoFiltering("nombreProducto.equals=" + DEFAULT_NOMBRE_PRODUCTO, "nombreProducto.equals=" + UPDATED_NOMBRE_PRODUCTO);
    }

    @Test
    @Transactional
    void getAllProductosByNombreProductoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where nombreProducto in
        defaultProductoFiltering(
            "nombreProducto.in=" + DEFAULT_NOMBRE_PRODUCTO + "," + UPDATED_NOMBRE_PRODUCTO,
            "nombreProducto.in=" + UPDATED_NOMBRE_PRODUCTO
        );
    }

    @Test
    @Transactional
    void getAllProductosByNombreProductoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where nombreProducto is not null
        defaultProductoFiltering("nombreProducto.specified=true", "nombreProducto.specified=false");
    }

    @Test
    @Transactional
    void getAllProductosByNombreProductoContainsSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where nombreProducto contains
        defaultProductoFiltering(
            "nombreProducto.contains=" + DEFAULT_NOMBRE_PRODUCTO,
            "nombreProducto.contains=" + UPDATED_NOMBRE_PRODUCTO
        );
    }

    @Test
    @Transactional
    void getAllProductosByNombreProductoNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where nombreProducto does not contain
        defaultProductoFiltering(
            "nombreProducto.doesNotContain=" + UPDATED_NOMBRE_PRODUCTO,
            "nombreProducto.doesNotContain=" + DEFAULT_NOMBRE_PRODUCTO
        );
    }

    @Test
    @Transactional
    void getAllProductosByPrecioIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where precio equals to
        defaultProductoFiltering("precio.equals=" + DEFAULT_PRECIO, "precio.equals=" + UPDATED_PRECIO);
    }

    @Test
    @Transactional
    void getAllProductosByPrecioIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where precio in
        defaultProductoFiltering("precio.in=" + DEFAULT_PRECIO + "," + UPDATED_PRECIO, "precio.in=" + UPDATED_PRECIO);
    }

    @Test
    @Transactional
    void getAllProductosByPrecioIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where precio is not null
        defaultProductoFiltering("precio.specified=true", "precio.specified=false");
    }

    @Test
    @Transactional
    void getAllProductosByPrecioIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where precio is greater than or equal to
        defaultProductoFiltering("precio.greaterThanOrEqual=" + DEFAULT_PRECIO, "precio.greaterThanOrEqual=" + UPDATED_PRECIO);
    }

    @Test
    @Transactional
    void getAllProductosByPrecioIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where precio is less than or equal to
        defaultProductoFiltering("precio.lessThanOrEqual=" + DEFAULT_PRECIO, "precio.lessThanOrEqual=" + SMALLER_PRECIO);
    }

    @Test
    @Transactional
    void getAllProductosByPrecioIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where precio is less than
        defaultProductoFiltering("precio.lessThan=" + UPDATED_PRECIO, "precio.lessThan=" + DEFAULT_PRECIO);
    }

    @Test
    @Transactional
    void getAllProductosByPrecioIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        // Get all the productoList where precio is greater than
        defaultProductoFiltering("precio.greaterThan=" + SMALLER_PRECIO, "precio.greaterThan=" + DEFAULT_PRECIO);
    }

    private void defaultProductoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProductoShouldBeFound(shouldBeFound);
        defaultProductoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductoShouldBeFound(String filter) throws Exception {
        restProductoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(producto.getId().intValue())))
            .andExpect(jsonPath("$.[*].clave").value(hasItem(DEFAULT_CLAVE)))
            .andExpect(jsonPath("$.[*].nombreProducto").value(hasItem(DEFAULT_NOMBRE_PRODUCTO)))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(DEFAULT_PRECIO.doubleValue())));

        // Check, that the count call also returns 1
        restProductoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductoShouldNotBeFound(String filter) throws Exception {
        restProductoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProducto() throws Exception {
        // Get the producto
        restProductoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto
        Producto updatedProducto = productoRepository.findById(producto.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProducto are not directly saved in db
        em.detach(updatedProducto);
        updatedProducto.clave(UPDATED_CLAVE).nombreProducto(UPDATED_NOMBRE_PRODUCTO).precio(UPDATED_PRECIO);

        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProducto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedProducto))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductoToMatchAllProperties(updatedProducto);
    }

    @Test
    @Transactional
    void putNonExistingProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, producto.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(producto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(producto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(producto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductoWithPatch() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto using partial update
        Producto partialUpdatedProducto = new Producto();
        partialUpdatedProducto.setId(producto.getId());

        partialUpdatedProducto.precio(UPDATED_PRECIO);

        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProducto))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProducto, producto), getPersistedProducto(producto));
    }

    @Test
    @Transactional
    void fullUpdateProductoWithPatch() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producto using partial update
        Producto partialUpdatedProducto = new Producto();
        partialUpdatedProducto.setId(producto.getId());

        partialUpdatedProducto.clave(UPDATED_CLAVE).nombreProducto(UPDATED_NOMBRE_PRODUCTO).precio(UPDATED_PRECIO);

        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProducto))
            )
            .andExpect(status().isOk());

        // Validate the Producto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductoUpdatableFieldsEquals(partialUpdatedProducto, getPersistedProducto(partialUpdatedProducto));
    }

    @Test
    @Transactional
    void patchNonExistingProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, producto.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(producto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(producto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProducto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(producto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Producto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProducto() throws Exception {
        // Initialize the database
        insertedProducto = productoRepository.saveAndFlush(producto);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the producto
        restProductoMockMvc
            .perform(delete(ENTITY_API_URL_ID, producto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Producto getPersistedProducto(Producto producto) {
        return productoRepository.findById(producto.getId()).orElseThrow();
    }

    protected void assertPersistedProductoToMatchAllProperties(Producto expectedProducto) {
        assertProductoAllPropertiesEquals(expectedProducto, getPersistedProducto(expectedProducto));
    }

    protected void assertPersistedProductoToMatchUpdatableProperties(Producto expectedProducto) {
        assertProductoAllUpdatablePropertiesEquals(expectedProducto, getPersistedProducto(expectedProducto));
    }
}

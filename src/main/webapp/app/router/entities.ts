/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/mhipstertest',
  component: Entities,
  children: [
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};

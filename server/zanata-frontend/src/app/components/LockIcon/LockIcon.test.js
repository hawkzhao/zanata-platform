/* global jest describe it expect */

import * as React from 'react'
import * as ReactDOMServer from 'react-dom/server'
import LockIcon from '.'
import {Icon} from '../../components'

describe('LockIcon', () => {
  it('renders a LockIcon when given a READONLY status', () => {
    const actual = ReactDOMServer.renderToStaticMarkup(
      <LockIcon status={'READONLY'} />
    )
    const expected = ReactDOMServer.renderToStaticMarkup(
      <Icon name='locked' className='s0' parentClassName='icon-locked' />
    )
    expect(actual).toEqual(expected)
  })
  it('renders an empty span when given an ACTIVE status', () => {
    const actual = ReactDOMServer.renderToStaticMarkup(
      <LockIcon status={'ACTIVE'} />
    )
    expect(actual).toEqual('')
  })
})

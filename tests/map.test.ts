import { test, expect } from '@playwright/test';
import path from 'path';

test('load a map', async ({ page }) => {
  await page.goto('localhost:8080/');

  const fileChooserPromise = page.waitForEvent('filechooser');

  await page.getByRole('button', { name: 'Import .xml delivery map file' }).click()

  const fileChooser = await fileChooserPromise;
  await fileChooser.setFiles(path.join(__dirname, 'petitPlan.xml'));

  await page.getByRole('button', { name: 'Confirm Map' }).click()
  await page.waitForTimeout(2000)

  await test.step('select number of couriers', async() => {
    await page.getByPlaceholder('Enter a number greater than').fill('2')
    await page.getByRole('button', { name: 'Initialize' }).click()
    await page.waitForTimeout(1000)
  })

  await page.waitForTimeout(1000)

  await test.step('load a tour', async() => {
    const fileChooserPromise = page.waitForEvent('filechooser');

    page.getByRole('button', { name: 'Import .xml delivery tour file' }).click()

    const fileChooser = await fileChooserPromise;
    await fileChooser.setFiles(path.join(__dirname, 'demandePetit1.xml'));

    await page.waitForTimeout(500)
    await page.getByRole('button', { name: 'Confirm Tour' }).click()
    await page.waitForTimeout(500)
  });

  await test.step('assign courier', async() => {
    await page.getByRole('button', { name: 'Assign' }).click()
    await page.waitForTimeout(500)
    await page.locator('div').filter({ hasText: 'Import .xml delivery tour file demandePetit1.xml Confirm Tour Delivery #1' }).getByRole('combobox').click()
    await page.waitForTimeout(500)
  })

  await test.step('select courier', async() => {
    
  })
});